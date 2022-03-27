package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static GUI.GUI.*;

public class MultiplayerLAN_Pane extends GamePane {

    MultiplayerLAN_Pane(){
        VBox mainPane= new VBox();

        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(15).doubleValue()));
       getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10,Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));

        final Text LAN_PLAY= new Text(" LAN PLAY");
        formatStandardText(LAN_PLAY,heightProperty(),30,Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));

        VBox topPane= new VBox(PLAY,LAN_PLAY);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setSpacing(0);
        topPane.setPadding(new Insets(40,0,0,40));

        StackPane bottomPane= new StackPane();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);


        nextSceneButton = new CustomButton(heightProperty(),"CREATE ROOM",10, Color.WHITE);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        nextSceneButton.setOnAction(e-> this.nextMenu());

        nextSceneButton2 = new CustomButton(heightProperty(),"JOIN ROOM",10, Color.WHITE);
        nextSceneButton2.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton2.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);


        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");
        previousSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        previousSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        bottomPane.getChildren().add(previousSceneButton);

        mainPane.getChildren().addAll(topPane,nextSceneButton,nextSceneButton2,bottomPane);
    }
    @Override
    public GamePane nextMenu() {//Create room
        return new MultiplayerGamePane(true,false);//todo
    }

    @Override
    public GamePane nextMenu2() {//Join room
        return new MultiplayerGamePane(false,false);//todo
    }

    @Override
    public GamePane previousMenu() {//PlayPane
        return new PlayPane();
    }

}
