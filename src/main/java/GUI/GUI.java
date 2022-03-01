package GUI;

import GUI.GameScene.MultiplayerGameScene;
import GUI.GameScene.SingleplayerGameScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private Scene startMenu;

    private Scene chessboard;
    private int buttonsToggled=0;



    @Override
    public void start(Stage primaryStage) {
        //Start Menu Elements

        //Chess Board elements


        //Chessboard scene & primary stage properties
        MultiplayerGameScene multiplayerGameScene= new MultiplayerGameScene(950,510);
        SingleplayerGameScene singleplayerGameScene= new SingleplayerGameScene(950,510);

        Scene currentScene= singleplayerGameScene;
        primaryStage.setScene(currentScene);
//        primaryStage.setFullScreen(true);
        primaryStage.setMinHeight(591);
        primaryStage.setMinWidth(1050);

        primaryStage.heightProperty().addListener(e-> {
            if(primaryStage.getWidth()<=primaryStage.getHeight() * 16.0 / 9.0)
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            primaryStage.setMinWidth(primaryStage.getHeight() * 16.0 / 9.0);
        });

        primaryStage.setTitle("Chess");
//        primaryStage.setFullScreenExitHint("Press F11 to exit full screen");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.show();

        //fullscreen command
        currentScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                primaryStage.setFullScreen(!primaryStage.isFullScreen());
                primaryStage.setWidth(primaryStage.getHeight() * 16.0 / 9.0);
            }
        });


    }
}
