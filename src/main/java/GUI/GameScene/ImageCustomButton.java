package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import static GUI.GUI.getImage;

public class ImageCustomButton extends Button {
    private ImageView graphic;
    private final DoubleBinding Y_BINDING;

    public ImageCustomButton(DoubleBinding Y_BINDING, String backgroundImage){
        this.Y_BINDING = Y_BINDING;
        graphic=new ImageView(getImage(backgroundImage));
        setGraphic(graphic);
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }
    public ImageCustomButton(DoubleBinding Y_BINDING){
        this.Y_BINDING = Y_BINDING;
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }

    public void setGraphic(ImageView graphic) {
        this.graphic = graphic;
        super.setGraphic(graphic);
        prefSizePropertyBind(Y_BINDING);
    }

    public void prefSizePropertyBind (DoubleBinding Ybinding){
        graphic.setPreserveRatio(true);
        graphic.fitHeightProperty().bind(Ybinding);
        prefHeightProperty().bind(Ybinding);
    }
}
