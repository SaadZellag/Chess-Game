package GUI;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import static GUI.GUI.formatStandardText;
import static GUI.GUI.glowEffect;

public class MainMenuPane extends GamePane{
    public MainMenuPane(){
        VBox mainPane= new VBox();
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(20).doubleValue()));
        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());

        mainPane.setAlignment(Pos.CENTER);
        getChildren().add(mainPane);

        final Text CHESS= new Text("CHESS");

        formatStandardText(CHESS,heightProperty(),5,Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));


        nextSceneButton = new CustomButton(heightProperty(),"PLAY",10, Color.WHITE);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        nextSceneButton.setOnAction(e-> this.nextMenu());

        nextSceneButton2 = new CustomButton(heightProperty(),"TIPS",10, Color.WHITE);
        nextSceneButton2.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton2.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        mainPane.getChildren().addAll(CHESS,nextSceneButton,nextSceneButton2);
    }
    @Override
    public GamePane nextMenu() {//Play
        return new PlayPane();
    }

    @Override
    public GamePane nextMenu2() {//Tips menu
        return null;//todo
    }
}
