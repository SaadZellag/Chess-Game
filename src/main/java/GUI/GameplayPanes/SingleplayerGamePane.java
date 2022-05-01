package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GamePane;
import engine.Engine;
import engine.MoveResult;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import java.util.concurrent.*;

import static GUI.GameMode.*;

public class SingleplayerGamePane extends MultiplayerGamePane {
    private final double difficulty;
    private static Thread engineThread;

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
        chessBoardPane.engineIsPaused=false;
        long engineTime=topRemainingTime;
        engineThread=new Thread(()->{
            Future<MoveResult> engineMove = Engine.getBestMove(chessBoardPane.internalBoard, engineTime);
            MoveResult bestMove ;
            try {
                if(engineThread.isInterrupted())
                    return;
                bestMove = engineMove.get();
                ConfidenceBar.percentage=bestMove.confidence;
                while (chessBoardPane.engineIsPaused) {
                    Thread.onSpinWait();
                    if(engineThread.isInterrupted())
                        return;
                }
                Platform.runLater(()-> {
                    if(engineThread.isInterrupted())
                        return;
                    chessBoardPane.animateMovePiece(bestMove.move);
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }catch (CancellationException ignored){
            }
        });
        engineThread.start();
    }
    public static void killEngine(ChessBoardPane chessBoardPane) {
        chessBoardPane.engineIsPaused=true;
        Engine.cancelCurrentSearch();
        if(engineThread!=null&&engineThread.isAlive())
            engineThread.interrupt();
    }

    @Override
    public GamePane nextMenu2() {
        killEngine(chessBoardPane);
        return new SingleplayerGamePane(whiteIsBottom,difficulty);//Rematch
    }



}
