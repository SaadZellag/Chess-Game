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
    private int buttonsToggled=0;



    @Override
    public void start(Stage primaryStage) {
        GameScene currentScene;


        //Start Menu Elements

        //Chess Board elements


        //Chessboard scene & primary stage properties
        MultiplayerGameScene multiplayerGameScene= new MultiplayerGameScene(950,510);
        SingleplayerGameScene singleplayerGameScene= new SingleplayerGameScene(950,510);

//        currentScene= singleplayerGameScene;
        primaryStage.setScene(multiplayerGameScene);


//        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);

        //Aspect ratio
        primaryStage.heightProperty().addListener(e-> {
            if(primaryStage.getWidth()<=primaryStage.getHeight() * 16.0 / 9.0)
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            primaryStage.setMinWidth(primaryStage.getHeight() * 16.0 / 9.0);
        });


        primaryStage.setTitle("Chess");
//        primaryStage.setFullScreenExitHint("Press F11 to exit full screen");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();

        //BGM
        MediaPlayer BGM = new MediaPlayer(new Media(getClass().getClassLoader().getResource("GUIResources/BGM.mp3").toString()));
        BGM.setCycleCount(MediaPlayer.INDEFINITE);
        BGM.play();
        HandleSceneSwitch(primaryStage,BGM);








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
