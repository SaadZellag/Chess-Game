package GUI.GameScene;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class SingleplayerGamePane extends MultiplayerGamePane {
    boolean whiteIsBottom;

    public SingleplayerGamePane(boolean whiteIsBottom) {
        super(whiteIsBottom,true);
        this.whiteIsBottom=whiteIsBottom;
        CustomButton undoButton= new CustomButton(heightProperty().divide(13),"UndoArrow.png");
        undoButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        undoButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        CustomButton redoButton= new CustomButton(heightProperty().divide(13),"RedoArrow.png");
        redoButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        redoButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
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
    public GamePane previousMenu() {
        return new MultiplayerGamePane(whiteIsBottom,false);
    }


}
