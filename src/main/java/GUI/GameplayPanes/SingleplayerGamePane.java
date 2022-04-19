package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.MenuPanes.MainMenuPane;
import engine.Engine;
import engine.MoveResult;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.concurrent.*;

import static GUI.GameMode.*;

public class SingleplayerGamePane extends MultiplayerGamePane {
    private final double difficulty;

    public SingleplayerGamePane(boolean whiteIsBottom, double difficulty) {
        super(whiteIsBottom, SOLO);
        this.difficulty=difficulty;
        CustomButton undoButton= new CustomButton(heightProperty().divide(13),"UndoArrow.png");

        CustomButton redoButton= new CustomButton(heightProperty().divide(13),"RedoArrow.png");
        mainPane.setSpacing(9);

        ConfidenceBar confidenceBar = new ConfidenceBar(heightProperty(), whiteIsBottom);

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
                Platform.runLater(()-> {
                    if(engineMove.isCancelled()) {
                        engineThread.shutdownNow();
                        return;
                    }
                    chessBoardPane.animateMovePiece(bestMove.move);
                });
            } catch (ExecutionException | InterruptedException e) {//todo can this be ignored too?
                e.printStackTrace();
            }catch (CancellationException ignored){
            }
        });
    }


    @Override
    public GamePane nextMenu2() {//Rematch
        return new SingleplayerGamePane(whiteIsBottom,difficulty);
    }
    @Override
    public GamePane previousMenu() {//Main Menu
        chessBoardPane.engineIsPaused=true;
        Engine.cancelCurrentSearch();
        return new MainMenuPane();
    }


}
