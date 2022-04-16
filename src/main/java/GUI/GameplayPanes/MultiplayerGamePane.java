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

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static GUI.GUI.*;

public class MultiplayerGamePane extends GamePane {
    public HBox mainPane = new HBox();
    public VBox pauseMenu;

    private final StackPane root = new StackPane();

    ObservableList<Move> moveHistoryList;
    public MoveHistoryField moveHistory;
    public static boolean whiteIsBottom;
    public ChessBoardPane chessBoardPane;

    private final VBox rightMostPane;

    public static long startingTime=10;

    private final Timer clockTimer= new Timer();
    public static long whiteRemainingTime = TimeUnit.MINUTES.toMillis(startingTime);
    public static long blackRemainingTime= TimeUnit.MINUTES.toMillis(startingTime);

    public MultiplayerGamePane(boolean whiteIsBottom, GameMode gameMode,long startingTime) {
        MultiplayerGamePane.whiteIsBottom =whiteIsBottom;
        MultiplayerGamePane.startingTime =startingTime;

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
        Date startTime= new Date();
        long minutes=TimeUnit.MILLISECONDS.toMinutes(startingTime);
        long millis =whiteRemainingTime-TimeUnit.MINUTES.toMillis(minutes);//
        lowerTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis)));
        upperTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis)));

        clockTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(chessBoardPane.internalBoard.isWhiteTurn()==whiteIsBottom){
                    long minutes=TimeUnit.MILLISECONDS.toMinutes(whiteRemainingTime-=1000);
                    long millis =whiteRemainingTime-TimeUnit.MINUTES.toMillis(minutes);//
                    Platform.runLater(()->lowerTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis))));
                }else{
                    long minutes=TimeUnit.MILLISECONDS.toMinutes(blackRemainingTime-=1000);
                    long millis =blackRemainingTime-TimeUnit.MINUTES.toMillis(minutes);//
                    Platform.runLater(()->upperTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis))));
                }
                if(blackRemainingTime==0||whiteRemainingTime==0){
                    cancel();
                    Platform.runLater(()->chessBoardPane.endGame(true));
                    System.out.println("this ran");
                }
            }
        },0, 1000L);
    }

    private void showPauseMenu(){
        mainPane.setDisable(true);
        mainPane.setEffect(new GaussianBlur(30));
        root.getChildren().add(pauseMenu);
    }

    private void endGame(){//todo give option to ask for a rematch
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
        if(!MoveGen.isInCheck(BitBoard.fromFEN(chessBoardPane.internalBoard.toFEN()))&&blackRemainingTime!=0&&whiteRemainingTime!=0){
            endMessage= new Text("DRAW");
            formatText(endMessage,heightProperty(),15,Color.BROWN,glowEffect(Color.RED,Color.CYAN));
        }



        pauseMenu.getChildren().remove(0);//removes resume button
        pauseMenu.getChildren().add(0,endMessage);

        pauseMenu.spacingProperty().bind(heightProperty().divide(8));



        pauseMenu.getChildren().add(1,nextSceneButton2);
        mainPane.getChildren().removeAll(moveHistory,rightMostPane);
        mainPane.getChildren().add(pauseMenu);

    }

    @Override
    public GamePane nextMenu() {
        return new MainMenuPane();//Main menu
    }

    @Override
    public GamePane nextMenu2() {
        return null;//todo rematch
    }
}
