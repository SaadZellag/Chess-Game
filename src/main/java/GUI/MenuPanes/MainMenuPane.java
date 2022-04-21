package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import javafx.scene.text.Text;

import static GUI.GUI.formatStandardText;

public class MainMenuPane extends MenuPane {
    public MainMenuPane(){
        heightProperty().addListener(e-> MIDDLE_PANE.setSpacing(heightProperty().divide(30).doubleValue()));

        final Text CHESS= new Text("CHESS");
        formatStandardText(CHESS,heightProperty(),5);


        nextSceneButton = new CustomButton(heightProperty(),"PLAY",10);
        nextSceneButton2 = new CustomButton(heightProperty(),"TIPS",10);

        MIDDLE_PANE.getChildren().addAll(CHESS,nextSceneButton,nextSceneButton2);
    }
    @Override
    public GamePane nextMenu() {//Play
        return new PlayPane();
    }

    @Override
    public GamePane nextMenu2() {//Tips menu
        return new TipsPane();
    }
}
