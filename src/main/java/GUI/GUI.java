package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import GUI.MenuPanes.*;
import engine.Engine;
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
import javafx.scene.input.KeyEvent;
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

import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GUI extends Application {

    public static ExecutorService serverThread;
    static public final long REFRESH_RATE = 200;
    public static final Duration TRANSITION_DURATION=Duration.seconds(0.4);
    private static GameServer gameServer;
    private static Client client;
    private static volatile boolean waitingForMove =true;
    public static final StackPane ROOT = new StackPane();

    public static final long STARTING_TIME =600000;
    private final MediaPlayer BGM = new MediaPlayer(new Media(getResource("BGM2.mp3")));
    public static void main(String[] args) {
        String OS = System.getProperty("os.name").toLowerCase();
        if (!OS.contains("mac")) {
            Engine.getCurrentSearch();
        }
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {

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
        BGM.setCycleCount(MediaPlayer.INDEFINITE);
        BGM.play();//todo make sure to uncomment this
        HandleSceneSwitch(initialPane);

        //prevent spaceBar from activating scene buttons
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, k -> {
            if ( k.getCode() == KeyCode.SPACE){
                k.consume();
            }
        });


        //fullscreen command
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            }
        });
        primaryStage.setFullScreenExitHint("Press F11 to toggle full screen");
//        primaryStage.setFullScreen(true);//todo make sure to uncomment this

        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);
        primaryStage.setTitle("Chess");

        primaryStage.setOnCloseRequest(e->{
            shutDownServer();
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

    }
    private static Move sentMove;

    /*
    Timeout status
    -1: waiting for second player to connect
     0: all players connected, ready to go
     1: timeout reached for the accept method in the GameServer class
     */
    private static int timeoutStatus;
    public static void launchServer(CreateRoomPane createRoomPane){
        timeoutStatus = -1;
        serverThread= Executors.newFixedThreadPool(2);
        serverThread.execute(()-> {
            //Game duration is in milliseconds
            gameServer= new GameServer(STARTING_TIME, 5000);
            timeoutStatus = gameServer.accept();

            if (timeoutStatus == 0) {
                Platform.runLater(() -> createRoomPane.nextSceneButton.fire());
            } else if (timeoutStatus == 1) {
                shutDownServer();
                Platform.runLater(()->{
                    if(!createRoomPane.pressedPreviousMenuButton){//to avoid displaying timeout when backing out from menu
                        createRoomPane.MIDDLE_PANE.getChildren().clear();
                        createRoomPane.MIDDLE_PANE.getChildren().addAll(createRoomPane.TIME_OUT,createRoomPane.RELOAD_BUTTON);
                    }
                });
            }
        });
        serverThread.execute(()-> {
            //Sleep to avoid race condition
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("Interrupted exception in server client thread.");
            }

            client = new Client("localhost");
            boolean isFirstMove=true;

            serverLoop:while(!serverThread.isShutdown()) {
                try{
                    // If not player connected in the timeout window, shutdown
                    if (timeoutStatus == 1) {
                        client.close();
                        serverThread.shutdownNow();
                    }
                    // If all players connected, continue as normal
                    if (timeoutStatus == 0) {
                        if (isFirstMove) {
                            while (waitingForMove) {
                                Thread.onSpinWait();
                                if(serverThread.isShutdown()){
                                    break serverLoop;
                                }
                            }
                            waitingForMove = true;
                            client.sendMove(sentMove);
                            isFirstMove=false;
                        }
                        Turn turn;
                        turn = client.receiveTurn();

                        if (turn == null) {
                            serverThread.shutdown();
                            break;
                        }
                        Turn finalTurn = turn;
                        Platform.runLater(() -> {
                            ((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.animateMovePiece(finalTurn.getMove());
                        });
                        MultiplayerGamePane.topRemainingTime=finalTurn.getBlackTimeLeft();
                        MultiplayerGamePane.bottomRemainingTime=finalTurn.getWhiteTimeLeft();
                        while (waitingForMove) {
                            Thread.onSpinWait();
                            if(serverThread.isShutdown()){
                                break serverLoop;
                            }
                        }
                        waitingForMove = true;
                        client.sendMove(sentMove);
                    }
                } catch (SocketException e) {
                    break;
                }
            }
            serverThread.shutdown();
            Platform.runLater(()-> {//send back to main menu if the match connection was severed mid-game
                if((ROOT.getChildren().get(0) instanceof MultiplayerGamePane)&&(!((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.gameEnded))
                    ((MultiplayerGamePane) ROOT.getChildren().get(0)).nextSceneButton.fire();
            });
        });
    }
    public static void joinServer(String ip){
        ExecutorService userThread= Executors.newSingleThreadExecutor();
        userThread.execute(()-> {
            client = new Client(ip);
            userLoop:while(!userThread.isShutdown()){
                Turn turn;
                try {
                    turn = client.receiveTurn();
                    if (turn == null) {
                        System.out.println("Received end game signal.");
                        break;
                    }
                    Turn finalTurn = turn;
                    Platform.runLater(()-> {
                        ((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.animateMovePiece(finalTurn.getMove());
                    });
                    MultiplayerGamePane.bottomRemainingTime=finalTurn.getBlackTimeLeft();
                    MultiplayerGamePane.topRemainingTime=finalTurn.getWhiteTimeLeft();
                    while (waitingForMove) {
                        Thread.onSpinWait();
                        if(userThread.isShutdown()){
                            break userLoop;
                        }
                    }
                    client.sendMove(sentMove);
                    waitingForMove=true;
                } catch (SocketException | NullPointerException e) {
                    break;
                }
            }
            userThread.shutdown();
            Platform.runLater(()-> {//send back to main menu if the match connection was severed mid-game
                if((ROOT.getChildren().get(0) instanceof MultiplayerGamePane)&&(!((MultiplayerGamePane) ROOT.getChildren().get(0)).chessBoardPane.gameEnded))
                    ((MultiplayerGamePane) ROOT.getChildren().get(0)).nextSceneButton.fire();
            });
        });
    }
    public static void shutDownServer(){
        if(gameServer!=null)
            gameServer.closeSocket();
        if(client!=null)
            client.endGame();
        waitingForMove=true;
    }
    public static void sendMove(Move move){
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

    public void HandleSceneSwitch(GamePane currentMenu){
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
            currentMenu.setMouseTransparent(true);
            GamePane nextMenu=  currentMenu.nextMenu();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu);
        });
        currentMenu.nextSceneButton2.setOnAction(e->{
            currentMenu.setMouseTransparent(true);
            GamePane nextMenu=  currentMenu.nextMenu2();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu);
        });
        currentMenu.nextSceneButton3.setOnAction(e->{
            currentMenu.setMouseTransparent(true);
            GamePane nextMenu=  currentMenu.nextMenu3();
            ROOT.getChildren().add(nextMenu);
            ROOT.getChildren().remove(currentMenu);
            HandleSceneSwitch(nextMenu);
        });
        //previousMenu
        currentMenu.previousSceneButton.setOnAction(e->{
            currentMenu.setMouseTransparent(true);
            GamePane previousMenu=  currentMenu.previousMenu();
            if(currentMenu instanceof MenuPane){
                FadeTransition centerTransition= new FadeTransition(TRANSITION_DURATION,((MenuPane)currentMenu).MIDDLE_PANE);
                centerTransition.setToValue(0);
                centerTransition.setOnFinished(f->{
                    ROOT.getChildren().add(previousMenu);
                    ROOT.getChildren().remove(currentMenu);
                    HandleSceneSwitch(previousMenu);
                });
                FadeTransition topTransition= new FadeTransition(TRANSITION_DURATION);
                if((currentMenu instanceof PlayPane)||(currentMenu instanceof TipsPane))
                    topTransition.setNode(((MenuPane)currentMenu).UPPER_TEXT);
                else
                    topTransition.setNode(((MenuPane)currentMenu).UPPER_SUBTEXT);
                topTransition.setToValue(0);

                if(currentMenu instanceof TipsPane){
                    FadeTransition chessTransition= new FadeTransition(TRANSITION_DURATION,((TipsPane)currentMenu).chessSide);
                    chessTransition.setToValue(0);
                    chessTransition.play();
                }

                topTransition.play();
                centerTransition.play();
            }else{
                ROOT.getChildren().add(previousMenu);
                ROOT.getChildren().remove(currentMenu);
                HandleSceneSwitch(previousMenu);
            }
        });
        if(BGM.isMute())
            currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Mute.png")));
        else
            currentMenu.MUTE_BUTTON.setGraphic(new ImageView(getImage("Unmute.png")));
        currentMenu.MUTE_BUTTON.getGraphic().setEffect(currentMenu.MUTE_BUTTON.idleGlowEffect);
    }

}

