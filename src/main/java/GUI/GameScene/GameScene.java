package GUI.GameScene;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public abstract class GameScene extends Scene {

    public GameScene(Parent root,double width, double height) {super(root,width,height);}
    public abstract GameScene previousScene();
    public abstract GameScene nextScene();
    public MuteButton muteButton;
    public Button nextSceneButton;
    public Button previousSceneButton;

}
