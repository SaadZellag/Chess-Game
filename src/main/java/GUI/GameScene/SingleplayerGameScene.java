package GUI.GameScene;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class SingleplayerGameScene extends MultiplayerGameScene {
    UndoButton undoButton= new UndoButton(heightProperty());
    RedoButton redoButton= new RedoButton(heightProperty());
    public SingleplayerGameScene(double width, double height) {
        super(width, height);
        mainPane.setSpacing(9);

        ConfidenceBar confidenceBar= new ConfidenceBar(heightProperty(),whiteIsBottom);
        mainPane.getChildren().add(0,confidenceBar);

        HBox undoRedoPane = new HBox();
        undoRedoPane.setSpacing(50);
        undoRedoPane.setAlignment(Pos.CENTER);
        undoRedoPane.getChildren().addAll(undoButton,redoButton);
        moveHistory.getChildren().add(undoRedoPane);
        moveHistory.prefSizePropertyBind(heightProperty().divide(1.1));
    }
    @Override
    public GameScene previousScene() {
        return new MultiplayerGameScene(getWidth(),getHeight());
    }


}
