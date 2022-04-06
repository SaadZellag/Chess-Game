package GUI;

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
    public DropShadow idleGlowEffect=glowEffect(Color.CYAN, Color.MAGENTA);
    public DropShadow hoveredGlowEffect=glowEffect(Color.BLACK, Color.RED);
    public Color fill= Color.WHITE;
    private DoubleBinding Y_BINDING;

    public CustomButton(ReadOnlyDoubleProperty propertyToListen, String text, double fontScale){//for text
        graphic=new Text (text);
        formatText((Text)graphic,propertyToListen,fontScale,fill,idleGlowEffect);
        setStyle("-fx-background-color:transparent");
        setPadding(new Insets(5,5,5,5));
        setGraphic(graphic);
        setOnMouseEntered(e->graphic.setEffect(hoveredGlowEffect));
        setOnMouseExited(e-> graphic.setEffect(idleGlowEffect));
    }
    public CustomButton(DoubleBinding Y_BINDING, String image){//for images
        this.Y_BINDING = Y_BINDING;
        graphic=new ImageView(getImage(image));
        setGraphic(graphic);
        graphic.setEffect(idleGlowEffect);
        setStyle("-fx-background-color:transparent");
        setPadding(new Insets(10,10,10,10));
        setOnMouseEntered(e->graphic.setEffect(hoveredGlowEffect));
        setOnMouseExited(e-> graphic.setEffect(idleGlowEffect));
        prefSizePropertyBind();
    }
    public  CustomButton(){

    }

    public CustomButton(DoubleBinding Y_BINDING){//custom button that had indeterminate graphic
        this.Y_BINDING = Y_BINDING;
        setStyle("-fx-background-color:transparent");
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
    public void setFill(Color fill){
        this.fill=fill;
        if(graphic instanceof Text)
            ((Text) graphic).setFill(fill);
    }

}
