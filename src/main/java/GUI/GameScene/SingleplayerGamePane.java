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
        chessBoardPane.heightProperty().addListener(e->{//makes it so the animations/dragging stay aligned even when the window is resized
            chessBoardPane.X_ANIMATION_OFFSET=getHeight()/22;
            chessBoardPane.Y_ANIMATION_OFFSET=getHeight()/22;

            chessBoardPane.X_DRAGGING_OFFSET=getHeight()/7.8;
            chessBoardPane.Y_DRAGGING_OFFSET=getHeight()/8;
        });

        undoButton.setOnAction(e->chessBoardPane.undo());
        redoButton.setOnAction(e->chessBoardPane.redo());
    }
    @Override
    public GamePane previousMenu() {
        return new MultiplayerGamePane();
    }


}
