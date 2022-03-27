package GUI;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import javafx.stage.Stage;

public class GUI extends Application {
    static public final long REFRESH_RATE = 120;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //Initial scene
        StackPane root= new StackPane();
        root.setBackground(getBackgroundImage("Main Background.png",root,true));
        GamePane initialPane= new MainMenuPane();
        root.getChildren().add(initialPane);
        Scene mainScene= new Scene(root,950,510);
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
        HandleSceneSwitch(root,initialPane,BGM);


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
            Platform.exit();
            System.exit(0);
        });
        primaryStage.show();

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
    public static void formatStandardText(Text text, ReadOnlyDoubleProperty propertyToListen, double fontScale, Color fill,DropShadow idleGlowEffect){
        text.setFill(fill);
        text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale));
        propertyToListen.addListener(e->text.setFont(Font.loadFont(getResource("standardFont.ttf"),propertyToListen.get()/fontScale)));
        text.setEffect(idleGlowEffect);
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

    public void HandleSceneSwitch(StackPane root, GamePane currentMenu,MediaPlayer BGM){

        //mute controller
        currentMenu.muteButton.setOnAction(e->BGM.setMute(!BGM.isMute()));
        currentMenu.muteButton.setOnMouseReleased(e->{
            if(BGM.isMute())
                currentMenu.muteButton.setGraphic(new ImageView(getImage("Mute.png")));
            else
                currentMenu.muteButton.setGraphic(new ImageView(getImage("Unmute.png")));
            currentMenu.muteButton.getGraphic().setEffect(currentMenu.muteButton.hoveredGlowEffect);
        });

        //nextMenus
        currentMenu.nextSceneButton.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu();
            root.getChildren().add(nextMenu);
            root.getChildren().remove(currentMenu);
            HandleSceneSwitch(root,nextMenu,BGM);
        });
        currentMenu.nextSceneButton2.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu2();
            root.getChildren().add(nextMenu);
            root.getChildren().remove(currentMenu);
            HandleSceneSwitch(root,nextMenu,BGM);
        });
        currentMenu.nextSceneButton3.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu3();
            root.getChildren().add(nextMenu);
            root.getChildren().remove(currentMenu);
            HandleSceneSwitch(root,nextMenu,BGM);
        });

        //previousMenu
        currentMenu.previousSceneButton.setOnAction(e->{
            GamePane previousMenu=  currentMenu.previousMenu();
            root.getChildren().add(previousMenu);
            root.getChildren().remove(currentMenu);
            HandleSceneSwitch(root,previousMenu,BGM);
        });
    }

}

