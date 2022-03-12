package GUI;

import GUI.GameScene.GamePane;
import GUI.GameScene.SingleplayerGamePane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        //Initial scene
        StackPane root= new StackPane();
        root.setAlignment(null);
        root.setBackground(getBackgroundImage("Main Background.png",root,true));
        GamePane initialPane= new SingleplayerGamePane();
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
//        BGM.play();
        HandleSceneSwitch(root,initialPane,BGM);


        //fullscreen command
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            }
        });
//        primaryStage.setFullScreenExitHint("Press F11 to exit full screen");
//        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }
    public static String getResource(String resourceName){
        return String.valueOf(GUI.class.getClassLoader().getResource("GUIResources/"+resourceName));
    }
    public static Image getImage(String imageName){
        return new Image(getResource(imageName));
    }
    public static Background getBackgroundImage(String imageName,Region region,boolean cover){
        BackgroundImage bImage = new BackgroundImage(getImage(imageName), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(region.getWidth(), region.getHeight(), true, true, true, cover));
        return new Background(bImage);
    }

    public void HandleSceneSwitch(StackPane root, GamePane currentMenu,MediaPlayer BGM){

        //mute controller
        currentMenu.muteButton.setOnAction(e->BGM.setMute(!BGM.isMute()));

        //nextMenu
        currentMenu.nextSceneButton.setOnAction(e->{
            GamePane nextMenu=  currentMenu.nextMenu();
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

