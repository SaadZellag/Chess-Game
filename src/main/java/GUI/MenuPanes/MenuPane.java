package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import static GUI.GUI.TRANSITION_DURATION;
import static GUI.GUI.formatStandardText;

public class MenuPane extends GamePane {
    public final VBox MAIN_PANE = new VBox();
    public final Text UPPER_TEXT= new Text();
    public final Text UPPER_SUBTEXT= new Text();
    public final VBox MIDDLE_PANE = new VBox();
    public FadeTransition centerTransition= new FadeTransition(TRANSITION_DURATION,MIDDLE_PANE);
    public FadeTransition topTransition = new FadeTransition(TRANSITION_DURATION, UPPER_SUBTEXT);
    MenuPane(){
        getChildren().add(MAIN_PANE);

        MAIN_PANE.prefWidthProperty().bind(widthProperty());
        MAIN_PANE.prefHeightProperty().bind(heightProperty());
        MAIN_PANE.setAlignment(Pos.TOP_CENTER);

        MIDDLE_PANE.maxWidthProperty().bind(widthProperty().divide(1.01));
        MIDDLE_PANE.setAlignment(Pos.CENTER);

        formatStandardText(UPPER_TEXT,heightProperty(),10);
        formatStandardText(UPPER_SUBTEXT,heightProperty(),30);


        VBox TOP_PANE = new VBox(UPPER_TEXT, UPPER_SUBTEXT);
        TOP_PANE.setAlignment(Pos.TOP_LEFT);
        TOP_PANE.setSpacing(0);
        TOP_PANE.setPadding(new Insets(20,0,0,40));


        HBox BOTTOM_PANE = new HBox();
        BOTTOM_PANE.setAlignment(Pos.BOTTOM_LEFT);
        BOTTOM_PANE.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(BOTTOM_PANE, Priority.ALWAYS);

        previousSceneButton = new CustomButton(heightProperty().divide(10),"UndoArrow.png");

        BOTTOM_PANE.getChildren().addAll(previousSceneButton, MUTE_BUTTON);
        MAIN_PANE.getChildren().addAll(TOP_PANE, MIDDLE_PANE, BOTTOM_PANE);
        playTransitions();
    }
    public void playTransitions(){
        centerTransition.setFromValue(0);
        centerTransition.setToValue(1);

        topTransition.setFromValue(0);
        topTransition.setToValue(1);

        topTransition.play();
        centerTransition.play();
    }
}
