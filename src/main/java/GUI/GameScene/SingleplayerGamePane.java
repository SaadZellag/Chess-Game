package GUI.GameScene;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class SingleplayerGamePane extends MultiplayerGamePane {
    CustomButton undoButton= new CustomButton(heightProperty().divide(15),heightProperty().divide(15),"board.png");
    CustomButton redoButton= new CustomButton(heightProperty().divide(15),heightProperty().divide(15),"board.png");
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

        chessBoardPane.X_DRAGGING_OFFSET=whiteIsBottom?70:20;

        undoButton.setOnAction(e->chessBoardPane.undo());
    }
    @Override
    public GamePane previousMenu() {
        return new MultiplayerGamePane();
    }


}
