package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class MuteButton extends Button {
    Image muteImage= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(muteImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(getWidth(), getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);
    public MuteButton(ReadOnlyDoubleProperty binding){
        prefSizePropertyBind(binding);
        setBackground(backGround);

    }

    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        prefWidthProperty().bind(binding.divide(11));
        prefHeightProperty().bind(binding.divide(11));
        setMinHeight(55);
        setMinWidth(55);
    }
}

