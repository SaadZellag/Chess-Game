package GUI.MenuPanes;
import GUI.GameMode;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;

import java.util.Timer;
import java.util.TimerTask;

import static GUI.GUI.*;
public class CreateRoomPane extends MenuPane {
    CreateRoomPane(){
        launchServer(this);
        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText(" CREATE ROOM");

        final Label WAITING= new Label("THE GAME WILL START AUTOMATICALLY ONCE AN OPPONENT IS FOUND");
        formatStandardText(WAITING,heightProperty(),15);
        heightProperty().addListener(e->WAITING.setPadding(new Insets(heightProperty().divide(5).doubleValue(),50,0,50)) );
        MIDDLE_PANE.getChildren().addAll(WAITING);

        previousSceneButton.setDisable(true);
        Timer timer= new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->previousSceneButton.setDisable(false));
            }
        },2000);


    }
    @Override
    public GamePane nextMenu() {//Create room
        return new MultiplayerGamePane(true, GameMode.ONLINE);//todo
    }


    @Override
    public GamePane previousMenu() {
        shutDownServer();
        return new MultiplayerLAN_Pane();
    }

}