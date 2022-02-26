package GUI;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
        MultiplayerGameScene multiplayerGameScene= new MultiplayerGameScene(800,450);

        primaryStage.setScene(multiplayerGameScene);
        primaryStage.setMinHeight(450);
        primaryStage.setMinWidth(800);



        primaryStage.show();


    }
}
