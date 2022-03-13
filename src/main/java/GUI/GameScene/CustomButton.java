package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import static GUI.GUI.getImage;

public class CustomButton extends Button {
    private final ImageView graphic;
    public CustomButton(DoubleBinding Ybinding, DoubleBinding Xbinding, String backgroundImage){
        graphic=new ImageView(getImage(backgroundImage));
        graphic.setPreserveRatio(true);
        setGraphic(graphic);
        prefSizePropertyBind(Ybinding,Xbinding);
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }

    public void prefSizePropertyBind (DoubleBinding Ybinding, DoubleBinding Xbinding){
        graphic.fitHeightProperty().bind(Ybinding);
        graphic.fitWidthProperty().bind(Xbinding);
//        prefWidthProperty().bind(binding);
        prefHeightProperty().bind(Ybinding);
    }
}
