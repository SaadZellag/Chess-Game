package GUI;

import GUI.GameScene.GameScene;
import GUI.GameScene.MultiplayerGameScene;
import GUI.GameScene.SingleplayerGameScene;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
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
        MultiplayerGameScene multiplayerGameScene= new MultiplayerGameScene(950,510);
        primaryStage.setScene(multiplayerGameScene);

        //Aspect ratio
        primaryStage.heightProperty().addListener(e-> {
            if(primaryStage.getWidth()<=primaryStage.getHeight() * 16.0 / 9.0)
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            primaryStage.setMinWidth(primaryStage.getHeight() * 16.0 / 9.0);
        });
        //BGM
        MediaPlayer BGM = new MediaPlayer(new Media(getClass().getClassLoader().getResource("GUIResources/BGM.mp3").toString()));
        BGM.setCycleCount(MediaPlayer.INDEFINITE);
//        BGM.play();
        HandleSceneSwitch(primaryStage,BGM);

//        primaryStage.setFullScreenExitHint("Press F11 to exit full screen");
//        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setTitle("Chess");
        primaryStage.show();
    }

    public void HandleSceneSwitch(Stage primaryStage, MediaPlayer BGM){
        GameScene currentScene=((GameScene)primaryStage.getScene());

        //fullscreen command
        primaryStage.getScene().setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            }
        });
        //mute controller
        currentScene.muteButton.setOnAction(e->BGM.setMute(!BGM.isMute()));

        //nextScene
        currentScene.nextSceneButton.setOnAction(e->{
            boolean t=primaryStage.isFullScreen();
            primaryStage.setScene(currentScene.nextScene());
            primaryStage.setFullScreen(t);
            HandleSceneSwitch(primaryStage,BGM);
        });

        //previousScene
        currentScene.previousSceneButton.setOnAction(e->{
            boolean t=primaryStage.isFullScreen();
            primaryStage.setScene(currentScene.previousScene());
            primaryStage.setFullScreen(t);
            HandleSceneSwitch(primaryStage,BGM);
        });
    }
}
