package GUI.GameScene;

import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public abstract class GamePane extends Pane {

    public abstract GamePane previousMenu();
    public abstract GamePane nextMenu();
    public MuteButton muteButton;
    public Button nextSceneButton;
    public Button previousSceneButton;

}
