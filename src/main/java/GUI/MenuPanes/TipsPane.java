package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static GUI.GUI.TRANSITION_DURATION;
import static GUI.GUI.formatStandardText;

public class TipsPane extends MenuPane {
    public VBox chessSide;
    TipsPane(){
        UPPER_TEXT.setText("TIPS");

        final Label INSTRUCTIONS= new Label("PLAY WITH THE WHITE PIECES TO SEE HOW THEY MOVE");
        final Label INSTRUCTIONS2= new Label("(PRESS R TO RESET THE BOARD)");
        formatStandardText(INSTRUCTIONS,heightProperty(),25);
        formatStandardText(INSTRUCTIONS2,heightProperty(),35);
        TipsChessBoardPane chessBoardPane= new TipsChessBoardPane(heightProperty().divide(1.2));

        chessSide= new VBox(INSTRUCTIONS,INSTRUCTIONS2,chessBoardPane);
        chessSide.setSpacing(10);
        chessSide.setPadding(new Insets(0,50,0,0));
        chessSide.setAlignment(Pos.CENTER);
        GridPane.setHgrow(chessSide,Priority.ALWAYS);
        setOnKeyPressed(e -> {
            if(e.getCode()== KeyCode.R){
                chessSide.getChildren().remove(2);
                chessSide.getChildren().add(new TipsChessBoardPane(heightProperty().divide(1.2)));
            }
        });

        nextSceneButton = new CustomButton(heightProperty(),"RULES",10);
        nextSceneButton2 = new CustomButton(heightProperty(),"BEGINNER\n     TIPS",10);

        MIDDLE_PANE.getChildren().addAll(nextSceneButton,nextSceneButton2);

        GridPane grid = new GridPane();
        grid.maxWidthProperty().bind(widthProperty().divide(1.001));

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        col1.setHalignment(HPos.CENTER);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        col2.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().addAll(col1, col2);

        grid.addRow(0,MAIN_PANE,chessSide);
        getChildren().clear();
        getChildren().add(grid);

        heightProperty().addListener(e-> {
            MIDDLE_PANE.setPadding(new Insets(heightProperty().divide(8).doubleValue(),0,0,0));
            MIDDLE_PANE.setSpacing(heightProperty().divide(15).doubleValue());
        });
    }
    @Override
    public void playTransitions(){
        centerTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(1)));
        centerTransition.setFromValue(0);
        centerTransition.setToValue(1);

        topTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(1)));
        topTransition.setFromValue(0);
        topTransition.setToValue(1);

        topTransition.play();
        centerTransition.play();
    }

    @Override
    public GamePane nextMenu() {//Rules Pane
        return new FileReadingPane("RULES","RulesParsed.txt");
    }

    @Override
    public GamePane nextMenu2() {//BEGINNER TIPS
        return new FileReadingPane("BEGINNER TIPS","ChessTipsParsed.txt");
    }

    @Override
    public GamePane previousMenu() {//Main menu
        return new MainMenuPane();
    }
}
