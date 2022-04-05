package GUI;

import GUI.GameplayPanes.SingleplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Random;

import static GUI.GUI.formatStandardText;
import static GUI.GUI.glowEffect;

public class SinglePlayerSettingsPane extends GamePane{
    private boolean whiteIsBottom;
    private final Slider slider;

    public SinglePlayerSettingsPane(){
        VBox mainPane= new VBox();

        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(42).doubleValue()));

        getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10);

        final Text SINGLE_PLAYER= new Text("SINGLE PLAYER");
        formatStandardText(SINGLE_PLAYER,heightProperty(),30);

        VBox topPane= new VBox(PLAY,SINGLE_PLAYER);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setSpacing(0);
        topPane.setPadding(new Insets(40,0,0,40));

        final Text PLAYER_COLOR= new Text(" PLAYER COLOR");
        formatStandardText(PLAYER_COLOR,heightProperty(),15);

        HBox bottomPane= new HBox();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);

        nextSceneButton = new CustomButton(heightProperty(),"START",13);
        nextSceneButton.setOnAction(e-> this.nextMenu());


        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");
        bottomPane.getChildren().add(previousSceneButton);

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

        slider= new Slider(0,1,0.7);
        slider.setPrefHeight(30);
        slider.maxWidthProperty().bind(widthProperty().divide(1.2));
        slider.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
//        slider.setMinorTickCount(10);
//        slider.setShowTickLabels(true);
//        slider.setShowTickMarks(true);
//        slider.setMajorTickUnit(25);

        final Text DIFFICULTY= new Text("ENGINE DIFFICULTY");
        formatStandardText(DIFFICULTY,heightProperty(),15);

        mainPane.getChildren().addAll(topPane,PLAYER_COLOR,startingColors,DIFFICULTY,slider,nextSceneButton,bottomPane);
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
