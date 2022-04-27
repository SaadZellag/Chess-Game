package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.GameplayPanes.SingleplayerGamePane;
import engine.Engine;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Random;

import static GUI.GUI.*;

public class SinglePlayerSettingsPane extends MenuPane {
    private boolean whiteIsBottom;
    private final Slider slider;

    public SinglePlayerSettingsPane(){
        heightProperty().addListener(e-> MIDDLE_PANE.setSpacing(heightProperty().divide(42).doubleValue()));

        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText("SINGLE PLAYER");

        final Text PLAYER_COLOR= new Text(" PLAYER COLOR");
        formatStandardText(PLAYER_COLOR,heightProperty(),15);

        nextSceneButton = new CustomButton(heightProperty(),"START",13);

        HBox startingColors= new HBox();
        startingColors.setAlignment(Pos.CENTER);
        startingColors.setSpacing(30);

        CustomButton white= new CustomButton(heightProperty().divide(10),"Pieces/W_King.png");
        white.setHoveredGlowEffect(Color.MAGENTA,Color.CYAN);

        CustomButton black= new CustomButton(heightProperty().divide(10),"Pieces/B_King.png");
        black.setIdleGlowEffect(Color.RED,Color.GOLD);
        black.setHoveredGlowEffect(Color.GOLD,Color.RED);

        CustomButton random= new CustomButton(heightProperty(),"?",10);
        Random rng= new Random();
        random.setIdleGlowEffect(Color.RED,Color.CYAN);
        random.setHoveredGlowEffect(Color.CYAN,Color.RED);

        startingColors.getChildren().addAll(black,white,random);

        random.setOnAction(e->{
            whiteIsBottom=rng.nextBoolean();
            random.setEffect(glowEffect(Color.GOLD,Color.WHITE));
            white.setEffect(null);
            black.setEffect(null);
        });
        black.setOnAction(e-> {
            whiteIsBottom = false;
            black.setEffect(glowEffect(Color.GOLD,Color.WHITE));
            white.setEffect(null);
            random.setEffect(null);
        });
        white.setOnAction(e-> {
            whiteIsBottom = true;
            white.setEffect(glowEffect(Color.GOLD,Color.WHITE));
            black.setEffect(null);
            random.setEffect(null);
        });

        slider= new Slider(1e-10,1,0.7);
        slider.maxWidthProperty().bind(widthProperty().divide(1.2));
        slider.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
        slider.setMinorTickCount(5);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(0.1);

        final Text DIFFICULTY= new Text("ENGINE DIFFICULTY");
        formatStandardText(DIFFICULTY,heightProperty(),15);
        MIDDLE_PANE.getChildren().addAll(PLAYER_COLOR,startingColors,DIFFICULTY,slider,nextSceneButton);
        random.fire();
    }
    @Override
    public GamePane nextMenu() {//Start
        return new SingleplayerGamePane(whiteIsBottom,slider.getValue());
    }

    @Override
    public GamePane previousMenu() {//PlayPane
        return new PlayPane();
    }
}
