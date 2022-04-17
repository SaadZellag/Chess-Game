package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GameMode;
import GUI.GamePane;
import GUI.MenuPanes.MainMenuPane;
import engine.Engine;
import engine.MoveResult;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.util.concurrent.*;

public class SingleplayerGamePane extends MultiplayerGamePane {
    private final boolean WHITE_IS_BOTTOM;
    private final double difficulty;

    public SingleplayerGamePane(boolean WHITE_IS_BOTTOM, double difficulty) {
        super(WHITE_IS_BOTTOM, GameMode.SOLO);
        this.WHITE_IS_BOTTOM = WHITE_IS_BOTTOM;
        this.difficulty=difficulty;
        CustomButton undoButton= new CustomButton(heightProperty().divide(13),"UndoArrow.png");

        CustomButton redoButton= new CustomButton(heightProperty().divide(13),"RedoArrow.png");
        mainPane.setSpacing(9);

        ConfidenceBar confidenceBar = new ConfidenceBar(heightProperty(), WHITE_IS_BOTTOM);

        mainPane.getChildren().add(0, confidenceBar);

        HBox undoRedoPane = new HBox();
        undoRedoPane.setSpacing(50);
        undoRedoPane.setAlignment(Pos.CENTER);
        undoRedoPane.getChildren().addAll(undoButton,redoButton);
        moveHistory.getChildren().add(undoRedoPane);
        moveHistory.prefSizePropertyBind(heightProperty().divide(1.1));


        undoButton.setOnAction(e->chessBoardPane.undo());
        redoButton.setOnAction(e->chessBoardPane.redo());

        Engine.setDifficulty(difficulty);
        if(!whiteIsBottom)
            startEngine(chessBoardPane);
    }

    public static void startEngine(ChessBoardPane chessBoardPane) {
        ExecutorService engineThread = Executors.newSingleThreadExecutor();
        engineThread.execute(()->{
            Future<MoveResult> engineMove = Engine.getBestMove(chessBoardPane.internalBoard, whiteIsBottom?blackRemainingTime:whiteRemainingTime);
            MoveResult bestMove ;
            try {
                bestMove = engineMove.get();
                ConfidenceBar.percentage=bestMove.confidence;
                while (chessBoardPane.engineIsPaused) {
                    Thread.onSpinWait();
                    if(engineMove.isCancelled()) {
                        engineThread.shutdownNow();
                        return;
                    }
                }
                Platform.runLater(()->chessBoardPane.animateMovePiece(bestMove.move));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }catch (CancellationException ignored){
            }
        });
    }


    @Override
    public GamePane nextMenu2() {//Rematch
        return new SingleplayerGamePane(WHITE_IS_BOTTOM,difficulty);
    }
    @Override
    public GamePane previousMenu() {//Main Menu
        return new MainMenuPane();
    }


}
