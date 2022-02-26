package GUI;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

public class SettingsButton extends Button {
    Image settingsImage= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(settingsImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(getWidth(), getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);
    public SettingsButton(){
        setBackground(backGround);

    }

    public void prefSizePropertyBind (DoubleBinding binding){
        prefWidthProperty().bind(binding);
        prefHeightProperty().bind(binding);
    }
}
