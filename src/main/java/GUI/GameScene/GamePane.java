package GUI.GameScene;

import javafx.scene.layout.Pane;

public abstract class GamePane extends Pane {

    public abstract GamePane previousMenu();
    public abstract GamePane nextMenu();
    public ImageCustomButton muteButton;
    public TextCustomButton nextSceneButton;
    public TextCustomButton previousSceneButton;

}
