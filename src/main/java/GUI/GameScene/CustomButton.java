package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import static GUI.GUI.getImage;

public class CustomButton extends Button {
    private ImageView graphic;
    private final DoubleBinding Y_BINDING;
    private final DoubleBinding X_BINDING;

    public CustomButton(DoubleBinding Y_BINDING, DoubleBinding X_BINDING, String backgroundImage){
        this.X_BINDING = X_BINDING;
        this.Y_BINDING = Y_BINDING;
        graphic=new ImageView(getImage(backgroundImage));
        setGraphic(graphic);
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }
    public CustomButton(DoubleBinding Y_BINDING, DoubleBinding X_BINDING){
        this.X_BINDING = X_BINDING;
        this.Y_BINDING = Y_BINDING;
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }

    public void setGraphic(ImageView graphic) {
        this.graphic = graphic;
        super.setGraphic(graphic);
        prefSizePropertyBind(Y_BINDING, X_BINDING);
    }

    public void prefSizePropertyBind (DoubleBinding Ybinding, DoubleBinding Xbinding){
        graphic.setPreserveRatio(true);
        graphic.fitHeightProperty().bind(Ybinding);
        graphic.fitWidthProperty().bind(Xbinding);
//        prefWidthProperty().bind(binding);
        prefHeightProperty().bind(Ybinding);
    }
}
