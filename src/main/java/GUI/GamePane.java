package GUI;

import javafx.scene.layout.Pane;

public class GamePane extends Pane {
    public GamePane nextMenu(){return null;}
    public GamePane nextMenu2(){return null;}
    public GamePane nextMenu3(){return null;}
    public GamePane previousMenu(){return null;}
    public CustomButton muteButton= new CustomButton(heightProperty().divide(11),"Unmute.png");
    public CustomButton nextSceneButton= new CustomButton();
    public CustomButton nextSceneButton2= new CustomButton();
    public CustomButton nextSceneButton3= new CustomButton();
    public CustomButton previousSceneButton= new CustomButton();
}
