package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import GUI.MenuPanes.*;
import game.Move;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import server.Client;
import server.GameServer;
import server.Turn;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GUI extends Application {
    static public final long REFRESH_RATE = 90;
    public static final Duration TRANSITION_DURATION=Duration.seconds(0.3);

    public static void main(String[] args) {
        launch(args);
    }
    private static GameServer gameServer;
    private static Client client;
    private static volatile boolean waitingForMove =true;
    private static final StackPane ROOT = new StackPane();
    @Override
    public void start(Stage primaryStage) {

        //Initial scene

        ROOT.setBackground(getBackgroundImage("Main Background.png", ROOT,true));
        GamePane initialPane= new MainMenuPane();
        ROOT.getChildren().add(initialPane);
        Scene mainScene= new Scene(ROOT,950,510);
        primaryStage.setScene(mainScene);

        //Aspect ratio
        primaryStage.heightProperty().addListener(e-> {
            if(primaryStage.getWidth()<=primaryStage.getHeight() * 16.0 / 9.0)
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            primaryStage.setMinWidth(primaryStage.getHeight() * 16.0 / 9.0);
        });

        //BGM
        MediaPlayer BGM = new MediaPlayer(new Media(getResource("BGM.mp3")));
        BGM.setCycleCount(MediaPlayer.INDEFINITE);
//        BGM.play();//todo make sure to uncomment this
        HandleSceneSwitch(initialPane,BGM);


        //fullscreen command
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            }
        });
//        primaryStage.setFullScreenExitHint("Press F11 to exit full screen");//todo make sure to uncomment this
//        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setTitle("Chess");
        primaryStage.setOnCloseRequest(e->{
            shutDownServer();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();
    }
    private static Move sentMove;
    public static void launchServer(CreateRoomPane createRoomPane){
        ExecutorService serverThread= Executors.newFixedThreadPool(2);
        serverThread.execute(()-> {
            gameServer= new GameServer();
            gameServer.accept();
            Platform.runLater(()->createRoomPane.nextSceneButton.fire());
        });
        serverThread.execute(()-> {
            client = new Client("localhost");
            int moves=0;
            while(true){
                if(moves==0){
                    while (waitingForMove) {
                        Thread.onSpinWait();
                    }
                    waitingForMove=true;
                    client.sendMove(sentMove);
                    System.out.println(sentMove.toString()+" was sent");
                    moves++;
                }
                Turn turn = client.receiveTurn();
                //null turn means end game, shutdown handled in the client class
                if(turn==null) {
                    break;
                }

                System.out.println(turn.getMove().toString()+" was received");
                Platform.runLater(()-> ((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.animateMovePiece(turn.getMove()));
                while (waitingForMove) {
                    Thread.onSpinWait();
                }
                client.sendMove(sentMove);
                System.out.println(sentMove.toString()+" was sent");
                waitingForMove=true;
            }
        });
    }
    public static void joinServer(String ip){
        ExecutorService userThread= Executors.newSingleThreadExecutor();
        userThread.execute(()-> {
            client = new Client(ip);
            while(true){
                Turn turn = client.receiveTurn();
                //null turn means end game, shutdown handled in the client class
                if(turn==null) {
                    break;
                }
                System.out.println(turn.getMove().toString()+" was received");
                Platform.runLater(()-> ((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.animateMovePiece(turn.getMove()));
                while (waitingForMove) {
                    Thread.onSpinWait();
                }
                client.sendMove(sentMove);
                waitingForMove=true;
            }
        });
    }
    public static GamePane shutDownServer(){//todo
//        if (client!=null)
            client.endGame();
        waitingForMove=true;
        return new MultiplayerLAN_Pane();
    }
    public static void sendMove(Move move){
        System.out.println(move.toString()+" should be sent");
        sentMove=move;
        waitingForMove =false;
    }



    public static String getResource(String resourceName){
        return String.valueOf(GUI.class.getClassLoader().getResource("GUI/"+resourceName));
    }
    public static Image getImage(String imageName){
        return new Image(getResource(imageName));
    }
    public static DropShadow glowEffect(Color c1, Color c2){
        DropShadow firstShadow = new DropShadow( 1,2, 2, c1);
        DropShadow secondShadow = new DropShadow( 2, -2, -2, c2);
        secondShadow.setInput(firstShadow);
        return secondShadow;
    }
    public static void formatText(Text text, ReadOnlyDoubleProperty propertyToListen, double fontScale, Color fill, DropShadow glowEffect){
        text.setFill(fill);
        text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale));
        propertyToListen.addListener(e->text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale)));
        text.setEffect(glowEffect);
    }
    public static void formatStandardText(Text text, ReadOnlyDoubleProperty propertyToListen, double fontScale){
        text.setFill(Color.color(0.24,0.24,0.24));
        text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale));
        propertyToListen.addListener(e->text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale)));
        text.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
    }
    public static void formatStandardText(Label text, ReadOnlyDoubleProperty propertyToListen, double fontScale){
        text.setTextFill(Color.color(0.24,0.24,0.24));
        text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale));
        propertyToListen.addListener(e->text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale)));
        text.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
        text.setWrapText(true);
        text.setTextAlignment(TextAlignment.CENTER);
    }

    public static Background getBackgroundImage(String imageName,Region region,boolean cover){
        BackgroundImage bImage = new BackgroundImage(
                getImage(imageName),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(region.getWidth(), region.getHeight(), false, false, true, cover));
        return new Background(bImage);
    }

    public void HandleSceneSwitch(GamePane currentMenu,MediaPlayer BGM){
        //mute controller
        currentMenu.MUTE_BUTTON.setOnAction(e->BGM.setMute(!BGM.isMute()));
        currentMenu.MUTE_BUTTON.setOnMouseReleased(e->{
            if(BGM.isMute())
                currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Mute.png")));
            else
                currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Unmute.png")));
            currentMenu.MUTE_BUTTON.getGraphic().setEffect(currentMenu.MUTE_BUTTON.hoveredGlowEffect);
        });

        //nextMenus
        currentMenu.nextSceneButton.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu,BGM);
        });
        currentMenu.nextSceneButton2.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu2();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu,BGM);
        });
        currentMenu.nextSceneButton3.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu3();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu,BGM);
        });

        //previousMenu
        currentMenu.previousSceneButton.setOnAction(e->{
            GamePane previousMenu=  currentMenu.previousMenu();
            if(currentMenu instanceof MenuPane){
                FadeTransition centerTransition= new FadeTransition(TRANSITION_DURATION,((MenuPane)currentMenu).MIDDLE_PANE);
                centerTransition.setToValue(0);
                centerTransition.setOnFinished(f->{
                    ROOT.getChildren().add(previousMenu);
                    ROOT.getChildren().remove(currentMenu);
                    HandleSceneSwitch(previousMenu,BGM);
                });
                FadeTransition topTransition= new FadeTransition(TRANSITION_DURATION);
                if(currentMenu instanceof PlayPane)
                    topTransition.setNode(((MenuPane)currentMenu).UPPER_TEXT);
                else
                    topTransition.setNode(((MenuPane)currentMenu).UPPER_SUBTEXT);
                topTransition.setToValue(0);

                topTransition.play();
                centerTransition.play();
            }else{
                ROOT.getChildren().add(previousMenu);
                ROOT.getChildren().remove(currentMenu);
                HandleSceneSwitch(previousMenu,BGM);
            }
        });
        if(BGM.isMute())
            currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Mute.png")));
        else
            currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Unmute.png")));
        currentMenu.MUTE_BUTTON.getGraphic().setEffect(currentMenu.MUTE_BUTTON.idleGlowEffect);
    }

}

