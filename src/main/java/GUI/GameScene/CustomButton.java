package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import static GUI.GUI.getImage;

public class CustomButton extends Button {
    private final ImageView graphic;
    public CustomButton(DoubleBinding binding, String backgroundImage){
        graphic=new ImageView(getImage(backgroundImage));
        graphic.setPreserveRatio(true);
        setGraphic(graphic);
        prefSizePropertyBind(binding);
        setBackground(null);
        setPadding(Insets.EMPTY);
    }

    public void prefSizePropertyBind (DoubleBinding binding){
        graphic.fitHeightProperty().bind(binding);
//        prefWidthProperty().bind(binding);
        prefHeightProperty().bind(binding);
    }
}
