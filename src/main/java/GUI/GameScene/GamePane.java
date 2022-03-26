package GUI.GameScene;

import javafx.scene.layout.Pane;

public abstract class GamePane extends Pane {

    public abstract GamePane previousMenu();
    public abstract GamePane nextMenu();
    public CustomButton muteButton= new CustomButton(heightProperty().divide(11),"Unmute.png");
    public CustomButton nextSceneButton;
    public CustomButton previousSceneButton;

}
