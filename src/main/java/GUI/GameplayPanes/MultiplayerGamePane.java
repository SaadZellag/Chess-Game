package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GameMode;
import GUI.GamePane;
import GUI.MenuPanes.MainMenuPane;
import engine.internal.BitBoard;
import engine.internal.MoveGen;
import game.Move;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static GUI.GUI.*;
import static GUI.GameMode.*;
import static GUI.GameplayPanes.SingleplayerGamePane.killEngine;

public class MultiplayerGamePane extends GamePane {
    HBox mainPane = new HBox();
    VBox pauseMenu;

    private final StackPane root = new StackPane();

    ObservableList<Move> moveHistoryList;
    MoveHistoryField moveHistory;
    public static boolean whiteIsBottom;
    public ChessBoardPane chessBoardPane;
    private final VBox rightMostPane;
    private final long STARTING_TIME =600000;
    private final Timer clockTimer= new Timer();
    static long topRemainingTime;
    static long bottomRemainingTime;
    private final GameMode gameMode;
    public MultiplayerGamePane(boolean whiteIsBottom, GameMode gameMode) {
        this.gameMode=gameMode;
        MultiplayerGamePane.whiteIsBottom =whiteIsBottom;
        topRemainingTime = STARTING_TIME;
        bottomRemainingTime = STARTING_TIME;

        //parent inherited buttons
        nextSceneButton = new CustomButton(heightProperty(),"MAIN MENU",15);
        previousSceneButton= new CustomButton(heightProperty(),"QUIT GAME",15);
        nextSceneButton2= new CustomButton(heightProperty(),"REMATCH",15);

        getChildren().add(root);
        root.getChildren().add(mainPane);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(5,20,5,20));


        //lefMostPane
        VBox leftMostPane = new VBox();

        chessBoardPane = new ChessBoardPane(heightProperty(), this::endGame,gameMode);
        if (!whiteIsBottom){
            chessBoardPane.setRotate(180);
            chessBoardPane.rotatePieces();
        }
        Text upperTimer= new Text();
        formatStandardText(upperTimer,heightProperty(),30);
        Text lowerTimer= new Text();
        formatStandardText(lowerTimer,heightProperty(),30);
        lowerTimer.setViewOrder(1);
        startTimers(upperTimer,lowerTimer);

        leftMostPane.getChildren().addAll(upperTimer,chessBoardPane,lowerTimer);

        leftMostPane.setAlignment(Pos.CENTER_LEFT);
        mainPane.getChildren().add(leftMostPane);

        //moveHistory
        moveHistoryList=chessBoardPane.moveHistoryList;
        moveHistory= new MoveHistoryField(moveHistoryList,heightProperty());
        mainPane.getChildren().add(moveHistory);
        moveHistory.setViewOrder(1);

        //rightMostPane
        rightMostPane= new VBox();
        mainPane.getChildren().add(rightMostPane);
        rightMostPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightMostPane.setSpacing(10);
        rightMostPane.setViewOrder(1);

        //muteButton
        rightMostPane.getChildren().add(MUTE_BUTTON);

        //pauseMenuButton
        CustomButton pauseMenuButton = new CustomButton(heightProperty().divide(7),"MenuIcon.png");
        rightMostPane.getChildren().add(pauseMenuButton);
        pauseMenuButton.setOnAction(e-> showPauseMenu());



        CustomButton resume= new CustomButton(heightProperty(),"RESUME",15);

        resume.setOnAction(e->{//remove pauseMenu
            mainPane.setEffect(null);
            mainPane.setDisable(false);
            root.getChildren().remove(pauseMenu);
        });

        //pauseMenu
        pauseMenu = new VBox();
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.spacingProperty().bind(heightProperty().divide(6));
        pauseMenu.setBackground(getBackgroundImage("RoundTextArea.png", pauseMenu,false));

        pauseMenu.getChildren().addAll(resume,nextSceneButton,previousSceneButton);
        pauseMenu.minWidthProperty().bind(chessBoardPane.heightProperty().multiply(0.9));
    }


    private void startTimers(Text upperTimer,Text lowerTimer) {
        lowerTimer.setText(formatTimeText(STARTING_TIME));
        upperTimer.setText(formatTimeText(STARTING_TIME));
        clockTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(chessBoardPane.internalBoard.isWhiteTurn()==whiteIsBottom){
                    Platform.runLater(()-> {
                        lowerTimer.setText(formatTimeText(bottomRemainingTime -= 1000/REFRESH_RATE));
                        upperTimer.setText(formatTimeText(topRemainingTime));
                    } );
                }else{
                    Platform.runLater(()-> {
                        upperTimer.setText(formatTimeText(topRemainingTime -= 1000/REFRESH_RATE));
                        lowerTimer.setText(formatTimeText(bottomRemainingTime));
                    } );
                }
                if(bottomRemainingTime ==0|| topRemainingTime ==0){
                    cancel();
                    Platform.runLater(()->chessBoardPane.endGame(true));
                }
            }
        },0, 1000L/REFRESH_RATE);
    }
    private String formatTimeText(long time){
        long minutes=TimeUnit.MILLISECONDS.toMinutes(time);
        long millis = time -TimeUnit.MINUTES.toMillis(minutes);//
        return String.format("%d:%02d", minutes, TimeUnit.MILLISECONDS.toSeconds(millis));
    }

    private void showPauseMenu(){
        mainPane.setDisable(true);
        mainPane.setEffect(new GaussianBlur(30));
        root.getChildren().add(pauseMenu);
    }

    private void endGame(){
        if(pauseMenu.getParent()!=null)
           ((CustomButton) pauseMenu.getChildren().get(0)).fire();

        switch (gameMode){
            case ONLINE: {
                shutDownServer();//todo do something else if we want option for rematch
                break;
            }
            case SOLO:{
                killEngine(chessBoardPane);
            }
            default:pauseMenu.getChildren().add(1,nextSceneButton2);
        }
        clockTimer.cancel();
        pauseMenu.setViewOrder(1);
        Text endMessage;
        if(chessBoardPane.internalBoard.isWhiteTurn()){
            endMessage= new Text("BLACK WON");
            formatText(endMessage,heightProperty(),15,Color.color(0.24, 0.24, 0.24),glowEffect(Color.RED,Color.GOLD));
        }else{
            endMessage= new Text("WHITE WON");
            formatText(endMessage,heightProperty(),15,Color.WHITE,glowEffect(Color.CYAN,Color.GOLD));
        }
        if(!MoveGen.isInCheck(BitBoard.fromFEN(chessBoardPane.internalBoard.toFEN()))&& bottomRemainingTime !=0&& topRemainingTime !=0){
            endMessage= new Text("DRAW");
            formatText(endMessage,heightProperty(),15,Color.BROWN,glowEffect(Color.RED,Color.CYAN));
        }
        pauseMenu.getChildren().remove(0);//removes resume button
        pauseMenu.getChildren().add(0,endMessage);

        pauseMenu.spacingProperty().bind(heightProperty().divide(8));

        mainPane.getChildren().removeAll(moveHistory,rightMostPane);
        mainPane.getChildren().add(pauseMenu);
    }

    @Override
    public GamePane nextMenu() {//send to main menu
        if(gameMode==ONLINE)
            shutDownServer();
        if(gameMode==SOLO){
            killEngine(chessBoardPane);
        }
        clockTimer.cancel();
        return new MainMenuPane();
    }

    @Override
    public GamePane nextMenu2() {
        return new MultiplayerGamePane(whiteIsBottom,gameMode);//rematch
    }
}
