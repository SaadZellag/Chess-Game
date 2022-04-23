package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;

import static GUI.GameMode.*;

public class PlayPane extends MenuPane {
    public PlayPane(){
        heightProperty().addListener(e-> {
            MIDDLE_PANE.setSpacing(heightProperty().divide(23).doubleValue());
            MIDDLE_PANE.setPadding(new Insets(heightProperty().divide(20).doubleValue(),0,0,0));
        });
        UPPER_TEXT.setText("PLAY");

        nextSceneButton = new CustomButton(heightProperty(),"SINGLE PLAYER",10);

        nextSceneButton2 = new CustomButton(heightProperty(),"MULTIPLAYER",10);

        nextSceneButton3 = new CustomButton(heightProperty(),"ONLINE PLAY",10);

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
        return new OnlinePlayPane();
    }

    @Override
    public GamePane previousMenu() {//Main menu
        return new MainMenuPane();
    }


}
