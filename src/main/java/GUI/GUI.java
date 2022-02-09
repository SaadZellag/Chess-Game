package GUI;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GUI extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    private Scene startMenu;

    private Scene chessboard;



    @Override
    public void start(Stage primaryStage) {
        //Start Menu Elements


        //Chess Board elements
        BorderPane mainPane= new BorderPane();
        GridPane centerPane = new GridPane();
        mainPane.setCenter(centerPane);
        centerPane.setAlignment(Pos.CENTER);
        chessboard = new Scene(mainPane,800,450);
        GridPane boardPane= new GridPane();
        StackPane[] tilesPanes= new StackPane[64];
        ToggleButton[] tilesButtons= new ToggleButton[64];
        int counter =0;
        //Create chess board
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                tilesPanes [counter]= new StackPane();
                tilesPanes[counter].minWidthProperty().bind(primaryStage.heightProperty().divide(9));
                tilesPanes[counter].minHeightProperty().bind(tilesPanes[counter].widthProperty());
                tilesButtons[counter]=new ToggleButton();
                tilesButtons[counter].minWidthProperty().bind(primaryStage.heightProperty().divide(9));
                tilesButtons[counter].minHeightProperty().bind(tilesPanes[counter].widthProperty());
                tilesButtons[counter].setBorder(Border.EMPTY);
                boardPane.add(tilesPanes[counter],i,j);
                tilesPanes [counter].getChildren().add(tilesButtons[counter]);
                counter++;
            }
        }
        centerPane.add(boardPane,0,0);
        primaryStage.setScene(chessboard);
        primaryStage.show();


    }
}
