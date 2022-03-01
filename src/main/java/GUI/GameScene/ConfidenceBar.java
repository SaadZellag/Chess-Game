package GUI.GameScene;

import javafx.animation.*;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ConfidenceBar extends GridPane {
    StackPane  stackPane= new StackPane();

    Rectangle top = new Rectangle(19,600, Color.BLACK);
    Rectangle bottom = new Rectangle(19,600,Color.WHITE);

    ConfidenceBar(ReadOnlyDoubleProperty binding,boolean whiteIsBottom){
        if (!whiteIsBottom){
            Rectangle temp=top;
            top=bottom;
            bottom=temp;
        }
        prefSizePropertyBind(binding);
        top.heightProperty().bind(binding.divide(2.2));
        stackPane.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().addAll(bottom,top); //this order will have to depend on which player is at the top
        getChildren().add(stackPane);

        setAlignment(Pos.CENTER);
        setOnMouseClicked(e->moveBar(Math.random()));


    }
    public void moveBar(double percentage){
        Thread t = new Thread(()->{
            top.heightProperty().unbind();
            double heightDifference= bottom.getHeight()*percentage-top.getHeight();
            for (double i = 0; i < 200; i++) {
                try {
                    top.setHeight(top.getHeight()+heightDifference/200);
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            top.heightProperty().bind(bottom.heightProperty().multiply(percentage));
        });
        t.start();
    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        bottom.heightProperty().bind(binding.divide(1.1));
        top.yProperty().bind(bottom.yProperty());
    }

}
