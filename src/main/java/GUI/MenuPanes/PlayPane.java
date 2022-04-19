package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;

import static GUI.GameMode.*;

public class PlayPane extends MenuPane {
    public PlayPane(){
        heightProperty().addListener(e-> MIDDLE_PANE.setSpacing(heightProperty().divide(23).doubleValue()));
        UPPER_TEXT.setText("PLAY");

        nextSceneButton = new CustomButton(heightProperty(),"SINGLE PLAYER",10);

        nextSceneButton2 = new CustomButton(heightProperty(),"LOCAL MULTIPLAYER",10);

        nextSceneButton3 = new CustomButton(heightProperty(),"LAN PLAY",10);

        MIDDLE_PANE.getChildren().addAll(nextSceneButton,nextSceneButton2,nextSceneButton3);
    }
    @Override
    public GamePane nextMenu() {//Single Player
        return new SinglePlayerSettingsPane();
    }

    @Override
    public GamePane nextMenu2() {//Local Multiplayer
        return new MultiplayerGamePane(true, LOCAL);
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
