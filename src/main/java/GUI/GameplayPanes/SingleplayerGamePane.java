package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.MainMenuPane;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SingleplayerGamePane extends MultiplayerGamePane {
    private final boolean whiteIsBottom;
    private final double difficulty;

    public SingleplayerGamePane(boolean whiteIsBottom,double difficulty) {
        super(whiteIsBottom,true);
        this.whiteIsBottom=whiteIsBottom;
        this.difficulty=difficulty;
        CustomButton undoButton= new CustomButton(heightProperty().divide(13),"UndoArrow.png");

        CustomButton redoButton= new CustomButton(heightProperty().divide(13),"RedoArrow.png");
        mainPane.setSpacing(9);

        ConfidenceBar confidenceBar= new ConfidenceBar(heightProperty(),whiteIsBottom);
        mainPane.getChildren().add(0,confidenceBar);

        HBox undoRedoPane = new HBox();
        undoRedoPane.setSpacing(50);
        undoRedoPane.setAlignment(Pos.CENTER);
        undoRedoPane.getChildren().addAll(undoButton,redoButton);
        moveHistory.getChildren().add(undoRedoPane);
        moveHistory.prefSizePropertyBind(heightProperty().divide(1.1));


        undoButton.setOnAction(e->chessBoardPane.undo());
        redoButton.setOnAction(e->chessBoardPane.redo());
    }

    @Override
    public GamePane nextMenu2() {//Rematch
        return new SingleplayerGamePane(whiteIsBottom,difficulty);
    }
    @Override
    public GamePane previousMenu() {//Main Menu
        return new MainMenuPane();
    }


}
