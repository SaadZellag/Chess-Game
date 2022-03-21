package GUI.GameScene;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static GUI.GUI.*;

public class TextCustomButton extends Button {
    private final Text graphic;
    private DropShadow idleGlowEffect=glowEffect(Color.TRANSPARENT, Color.TRANSPARENT);
    private DropShadow hoveredGlowEffect=glowEffect(Color.TRANSPARENT, Color.TRANSPARENT);

    TextCustomButton(ReadOnlyDoubleProperty propertyToListen, String text, double fontScale, Color fill){

        graphic=new Text (text);
        formatStandardText(graphic,propertyToListen,fontScale,fill,idleGlowEffect);
        setBackground(null);
        setPadding(new Insets(5,5,5,5));
        setGraphic(graphic);
        setOnMouseEntered(e->graphic.setEffect(hoveredGlowEffect));
        setOnMouseExited(e-> graphic.setEffect(idleGlowEffect));
    }
    public void setIdleGlowEffect(Color color1, Color color2){
        idleGlowEffect=glowEffect(color1,color2);
        graphic.setEffect(idleGlowEffect);
    }
    public void setHoveredGlowEffect(Color color1, Color color2){
        hoveredGlowEffect=glowEffect(color1, color2);
    }

}
