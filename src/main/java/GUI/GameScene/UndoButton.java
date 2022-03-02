package GUI.GameScene;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class UndoButton extends Button {
    Image undoImage= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(undoImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(getWidth(), getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);
    public UndoButton(ReadOnlyDoubleProperty binding){
        prefSizePropertyBind(binding);
        setBackground(backGround);
    }

    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        prefWidthProperty().bind(binding.divide(15));
        prefHeightProperty().bind(binding.divide(15));
    }
    public void undo(){

    }
}
