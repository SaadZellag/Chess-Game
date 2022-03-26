package GUI.GameScene;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static GUI.GUI.*;

public class CustomButton extends Button {
    private  Node graphic;
    public DropShadow idleGlowEffect=glowEffect(Color.TRANSPARENT, Color.TRANSPARENT);
    public DropShadow hoveredGlowEffect=glowEffect(Color.TRANSPARENT, Color.TRANSPARENT);
    private DoubleBinding Y_BINDING;

    public CustomButton(ReadOnlyDoubleProperty propertyToListen, String text, double fontScale, Color fill){//for text
        graphic=new Text (text);
        formatStandardText((Text)graphic,propertyToListen,fontScale,fill,idleGlowEffect);
        setBackground(null);
        setPadding(new Insets(5,5,5,5));
        setGraphic(graphic);
        setOnMouseEntered(e->graphic.setEffect(hoveredGlowEffect));
        setOnMouseExited(e-> graphic.setEffect(idleGlowEffect));
    }
    public CustomButton(DoubleBinding Y_BINDING, String backgroundImage){//for images
        this.Y_BINDING = Y_BINDING;
        graphic=new ImageView(getImage(backgroundImage));
        setGraphic(graphic);
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->graphic.setEffect(hoveredGlowEffect));
        setOnMouseExited(e-> graphic.setEffect(idleGlowEffect));
        prefSizePropertyBind();
    }

    public CustomButton(DoubleBinding Y_BINDING){
        this.Y_BINDING = Y_BINDING;
        setBackground(null);
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->setEffect(new ColorAdjust(0,0.2,-0.2,0)));
        setOnMouseExited(e->setEffect(new ColorAdjust(0,0,0,0)));
    }

    public void setGraphic(ImageView graphic) {
        this.graphic = graphic;
        super.setGraphic(graphic);
        prefSizePropertyBind();
    }

    public void prefSizePropertyBind (){
        if(graphic instanceof ImageView) {
            ((ImageView)graphic).setPreserveRatio(true);
            ((ImageView)graphic).fitHeightProperty().bind(Y_BINDING);
        }
        prefHeightProperty().bind(Y_BINDING);
    }
    public void setIdleGlowEffect(Color color1, Color color2){
        idleGlowEffect=glowEffect(color1,color2);
        graphic.setEffect(idleGlowEffect);
    }
    public void setHoveredGlowEffect(Color color1, Color color2){
        hoveredGlowEffect=glowEffect(color1, color2);
    }

}
