package GUI.GameScene;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class ChessBoardPane extends GridPane {

    private StackPane[] tileStacks= new StackPane[64];
    private ToggleButton[] buttonTiles= new ToggleButton[64];

    Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(getWidth(), getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);

    private String toggledColor = "grey";
    private String untoggledColor = "transparent";
    public ChessBoardPane(ReadOnlyDoubleProperty binding){
        setAlignment(Pos.CENTER);
        setBackground(backGround);
        //Create chess board
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                int index= i+8*j;
                //TileStack properties
                double ratio=9;
                tileStacks [index]= new StackPane();
                tileStacks[index].setMinHeight(getMinHeight()/ratio);
                tileStacks[index].setMinWidth(tileStacks[index].getMinHeight());
                tileStacks[index].prefWidthProperty().bind(widthProperty().divide(ratio));
                tileStacks[index].prefHeightProperty().bind(widthProperty().divide(ratio));
                //ButtonTile properties
                buttonTiles[index]=new ToggleButton();
                buttonTiles[index].prefWidthProperty().bind(widthProperty().divide(ratio));
                buttonTiles[index].prefHeightProperty().bind(widthProperty().divide(ratio));
                buttonTiles[index].setMinHeight(tileStacks[index].getMinHeight());
                buttonTiles[index].setMinWidth(tileStacks[index].getMinHeight());
                buttonTiles[index].setOpacity(0.5);
                buttonTiles[index].setStyle("-fx-background-color:"+untoggledColor);

                add(tileStacks[index],i,j);
                tileStacks [index].getChildren().add(buttonTiles[index]);

                buttonTiles[index].setOnAction(e-> tileController(index,e));
            }
        }
        prefSizePropertyBind(binding);
    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        prefHeightProperty().bind(binding.divide(1.1));
        prefWidthProperty().bind(prefHeightProperty());
        minHeightProperty().bind(binding.divide(1.1));
        minWidthProperty().bind(prefHeightProperty());
    }
    private void tileController(int index, ActionEvent e){
        System.out.println(index);
        if(buttonTiles[index].isSelected())
            buttonTiles[index].setStyle("-fx-background-color:"+toggledColor);
        else
            buttonTiles[index].setStyle("-fx-background-color:"+untoggledColor);

    }
}
