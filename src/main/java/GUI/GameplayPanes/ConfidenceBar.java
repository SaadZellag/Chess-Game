package GUI.GameplayPanes;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Timer;
import java.util.TimerTask;

import static GUI.GUI.REFRESH_RATE;

public class ConfidenceBar extends GridPane {
    StackPane  stackPane= new StackPane();

    Rectangle top = new Rectangle(19,600, Color.BLACK);
    Rectangle bottom = new Rectangle(19,600,Color.WHITE);

    public static double percentage = 0.5;


    // Recommended 0 (non-inclusive) and 1 to determine how fast the rectangle updates
    // Can be set over 1, but it will be very slow
    static final double GROWTH_DELAY = 0.3;


    ConfidenceBar(ReadOnlyDoubleProperty binding, boolean whiteIsBottom){
        if (!whiteIsBottom){
            Rectangle temp=top;
            top=bottom;
            bottom=temp;
        }
        prefSizePropertyBind(binding);
        percentage=0.5;


        stackPane.setAlignment(Pos.TOP_CENTER);
        stackPane.getChildren().addAll(bottom,top);
        getChildren().add(stackPane);

        setAlignment(Pos.CENTER);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                double currentPercentage = top.getHeight()/bottom.getHeight();
                double slope = (percentage - currentPercentage) / REFRESH_RATE * Math.abs(1 / GROWTH_DELAY);
                Platform.runLater(() -> top.heightProperty().bind(bottom.heightProperty().multiply(currentPercentage+slope)));
            }
        }, 10000, 1000 / REFRESH_RATE);

    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        bottom.heightProperty().bind(binding.divide(1.1));
        bottom.widthProperty().bind(binding.divide(24));

        top.yProperty().bind(bottom.yProperty());
        top.heightProperty().bind(binding.divide(2.2));
        top.widthProperty().bind(binding.divide(24));
    }

}
