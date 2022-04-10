package GUI.MenuPanes;
import GUI.GameMode;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import static GUI.GUI.formatStandardText;
import static GUI.GameMode.ONLINE;

public class CreateRoomPane extends MenuPane {
//    GameServer gameServer= new GameServer();
    CreateRoomPane(){
//        Client serverClient= new Client("localhost");
//        Thread t= new Thread(gameServer::accept);

        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText(" CREATE ROOM");

        final Label WAITING= new Label("THE GAME WILL START AUTOMATICALLY ONCE AN OPPONENT IS FOUND");
        formatStandardText(WAITING,heightProperty(),15);
        heightProperty().addListener(e->WAITING.setPadding(new Insets(heightProperty().divide(5).doubleValue(),50,0,50)) );
        MIDDLE_PANE.getChildren().addAll(WAITING);
    }
    @Override
    public GamePane nextMenu() {//Create room
        return new MultiplayerGamePane(true, GameMode.ONLINE);//todo
    }


    @Override
    public GamePane previousMenu() {
        return new MultiplayerLAN_Pane();//todo need to close server when exiting
    }

}