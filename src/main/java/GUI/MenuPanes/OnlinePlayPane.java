package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;

public class OnlinePlayPane extends MenuPane {

    public OnlinePlayPane(){
        heightProperty().addListener(e-> {
            MIDDLE_PANE.setSpacing(heightProperty().divide(18).doubleValue());
            MAIN_PANE.setSpacing(heightProperty().divide(8).doubleValue());
        });

        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText(" ONLINE PLAY");

        nextSceneButton = new CustomButton(heightProperty(),"CREATE ROOM",10);
        nextSceneButton2 = new CustomButton(heightProperty(),"JOIN ROOM",10);

        MIDDLE_PANE.getChildren().addAll(nextSceneButton,nextSceneButton2);
    }
    @Override
    public GamePane nextMenu() {//Create room
        return new CreateRoomPane();
    }

    @Override
    public GamePane nextMenu2() {//Join room
        return new JoinRoomPane();
    }

    @Override
    public GamePane previousMenu() {//PlayPane
        return new PlayPane();
    }

}
