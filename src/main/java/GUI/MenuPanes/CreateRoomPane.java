package GUI.MenuPanes;
import GUI.CustomButton;
import GUI.GameMode;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import server.IdGenerator;

import java.util.Timer;
import java.util.TimerTask;

import static GUI.GUI.*;
public class CreateRoomPane extends MenuPane {
    private final IdGenerator GENERATOR = new IdGenerator();
    private final String ROOM_NAME=GENERATOR.ipToID(GENERATOR.getIp());
    public final Label WAITING= new Label("YOUR ROOM NAME IS: "+ROOM_NAME+"\n\nTHE GAME WILL START AUTOMATICALLY ONCE AN OPPONENT IS FOUND");
    public final Label TIME_OUT= new Label("ROOM HAS TIMED OUT...\n");
    public final CustomButton RELOAD_BUTTON = new CustomButton(heightProperty(),"RELOAD ROOM",15);
    CreateRoomPane(){




        launchServer(this);
        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText(" CREATE ROOM");

        formatStandardText(WAITING,heightProperty(),15);
        formatStandardText(TIME_OUT,heightProperty(),15);

        heightProperty().addListener(e-> {
            WAITING.setPadding(new Insets(heightProperty().divide(10).doubleValue(), 50, 0, 50));
            TIME_OUT.setPadding(new Insets(heightProperty().divide(5).doubleValue(), 50, 0, 50));
        });
        MIDDLE_PANE.getChildren().add(WAITING);
        RELOAD_BUTTON.setOnAction(e->{
            delayBackButton();
            MIDDLE_PANE.getChildren().clear();
            MIDDLE_PANE.getChildren().add(WAITING);
            launchServer(this);
        });
        delayBackButton();
    }
    public void delayBackButton(){
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
    public GamePane nextMenu() {
        return new MultiplayerGamePane(true, GameMode.ONLINE);
    }

    public boolean isPreviousMenuCall=false;
    @Override
    public GamePane previousMenu() {
        isPreviousMenuCall=true;
        shutDownServer();
        return new MultiplayerLAN_Pane();
    }

}