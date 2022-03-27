package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static GUI.GUI.formatStandardText;
import static GUI.GUI.glowEffect;

public class PlayPane extends GamePane{
    public PlayPane(){

        VBox mainPane= new VBox();


        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(18).doubleValue()));

        getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10,Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));
        VBox topPane= new VBox(PLAY);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setPadding(new Insets(40,0,0,40));


        HBox bottomPane= new HBox();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);

        nextSceneButton = new CustomButton(heightProperty(),"SINGLE PLAYER",10, Color.WHITE);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        nextSceneButton.setOnAction(e-> this.nextMenu());

        nextSceneButton2 = new CustomButton(heightProperty(),"LOCAL MULTIPLAYER",10, Color.WHITE);
        nextSceneButton2.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton2.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        nextSceneButton3 = new CustomButton(heightProperty(),"LAN PLAY",10, Color.WHITE);
        nextSceneButton3.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton3.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");
        previousSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        previousSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        bottomPane.getChildren().add(previousSceneButton);



        mainPane.getChildren().addAll(topPane,nextSceneButton,nextSceneButton2,nextSceneButton3,bottomPane);
    }
    @Override
    public GamePane nextMenu() {//Single Player
        return new SinglePlayerSettingsPane();
    }

    @Override
    public GamePane nextMenu2() {//Local Multiplayer
        return new MultiplayerGamePane(true,true);
    }

    @Override
    public GamePane nextMenu3() {//LAN Play
        return new MultiplayerLAN_Pane();
    }

    @Override
    public GamePane previousMenu() {//Main menu
        return new MainMenuPane();
    }


}
