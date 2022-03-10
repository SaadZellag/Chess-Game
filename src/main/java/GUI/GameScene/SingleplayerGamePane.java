package GUI.GameScene;

import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class SingleplayerGamePane extends MultiplayerGamePane {
    UndoButton undoButton= new UndoButton(heightProperty());
    RedoButton redoButton= new RedoButton(heightProperty());
    public SingleplayerGamePane() {
        mainPane.setSpacing(9);

        ConfidenceBar confidenceBar= new ConfidenceBar(heightProperty(),whiteIsBottom);
        mainPane.getChildren().add(0,confidenceBar);

        HBox undoRedoPane = new HBox();
        undoRedoPane.setSpacing(50);
        undoRedoPane.setAlignment(Pos.CENTER);
        undoRedoPane.getChildren().addAll(undoButton,redoButton);
        moveHistory.getChildren().add(undoRedoPane);
        moveHistory.prefSizePropertyBind(heightProperty().divide(1.1));

        chessBoardPane.X_DRAGGING_OFFSET=70;
        chessBoardPane.ROTATED_X_DRAGGING_OFFSET=40;
    }



}
