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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SingleplayerGamePane extends MultiplayerGamePane {
    private final boolean WHITE_IS_BOTTOM;
    private final double difficulty;
    private final ConfidenceBar CONFIDENCE_BAR;
    public SingleplayerGamePane(boolean WHITE_IS_BOTTOM, double difficulty, long startingTime) {
        super(WHITE_IS_BOTTOM, GameMode.SOLO,startingTime);
        this.WHITE_IS_BOTTOM = WHITE_IS_BOTTOM;
        this.difficulty=difficulty;
        CustomButton undoButton= new CustomButton(heightProperty().divide(13),"UndoArrow.png");

        CustomButton redoButton= new CustomButton(heightProperty().divide(13),"RedoArrow.png");
        mainPane.setSpacing(9);

        CONFIDENCE_BAR= new ConfidenceBar(heightProperty(), WHITE_IS_BOTTOM);

        mainPane.getChildren().add(0,CONFIDENCE_BAR);

        HBox undoRedoPane = new HBox();
        undoRedoPane.setSpacing(50);
        undoRedoPane.setAlignment(Pos.CENTER);
        undoRedoPane.getChildren().addAll(undoButton,redoButton);
        moveHistory.getChildren().add(undoRedoPane);
        moveHistory.prefSizePropertyBind(heightProperty().divide(1.1));


        undoButton.setOnAction(e->chessBoardPane.undo());
        redoButton.setOnAction(e->chessBoardPane.redo());

        chessBoardPane.setDifficulty(difficulty);
        startEngine();
    }
    private void startEngine() {
        ExecutorService engineThread= Executors.newSingleThreadExecutor();
        engineThread.execute(()->{
            while(!chessBoardPane.isReceivingMove) {
                Thread.onSpinWait();
            }
            MoveResult bestMove= Engine.getBestMove(chessBoardPane.internalBoard, WHITE_IS_BOTTOM ?blackRemainingTime : whiteRemainingTime);
            CONFIDENCE_BAR.setPercentage(WHITE_IS_BOTTOM ?bestMove.confidence:1-bestMove.confidence);//FIXME
            while (chessBoardPane.engineIsPaused) {
                Thread.onSpinWait();
                if(chessBoardPane.needsEngineKilled) {//fixme this is horrible code
                    startEngine();
                    engineThread.shutdownNow();
                }
            }
            Platform.runLater(()-> {
                chessBoardPane.isReceivingMove=false;
                chessBoardPane.animateMovePiece(bestMove.move);
                startEngine();
            });

        });
    }

    @Override
    public GamePane nextMenu2() {//Rematch
        return new SingleplayerGamePane(WHITE_IS_BOTTOM,difficulty,startingTime);
    }
    @Override
    public GamePane previousMenu() {//Main Menu
        return new MainMenuPane();
    }


}
