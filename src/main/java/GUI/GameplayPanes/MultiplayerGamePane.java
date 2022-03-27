package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GamePane;
import GUI.MainMenuPane;
import GUI.PlayPane;
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
import java.util.concurrent.TimeUnit;

import static GUI.GUI.*;

public class MultiplayerGamePane extends GamePane {
    public HBox mainPane = new HBox();
    public VBox pauseMenu;

    private final StackPane root = new StackPane();

    ObservableList<Move> moveHistoryList;
    public MoveHistoryField moveHistory;
    public boolean whiteIsBottom;
    public ChessBoardPane chessBoardPane;

    private final VBox rightMostPane;

    final long[] whiteRemainingTime = {TimeUnit.MINUTES.toMillis(10)};
    final long[] blackRemainingTime= {TimeUnit.MINUTES.toMillis(10)};

    private final Timer clockTimer= new Timer();
    public MultiplayerGamePane(boolean whiteIsBottom,boolean isLocalGame) {
        this.whiteIsBottom=whiteIsBottom;


        //parent inherited buttons
        muteButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        muteButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        previousSceneButton = new CustomButton(heightProperty(),"MAIN MENU",15,Color.WHITE);
        previousSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        previousSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        nextSceneButton= new CustomButton(heightProperty(),"QUIT GAME",15,Color.WHITE);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        nextSceneButton2= new CustomButton(heightProperty(),"REMATCH",15,Color.WHITE);
        nextSceneButton2.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton2.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);


        getChildren().add(root);
        root.getChildren().add(mainPane);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(5,20,5,20));


        //lefMostPane
        VBox leftMostPane = new VBox();

        chessBoardPane = new ChessBoardPane(heightProperty(), this::endGame,isLocalGame);
        if (!whiteIsBottom){
            chessBoardPane.setRotate(180);
            chessBoardPane.rotatePieces();
        }
        Text upperTimer= new Text("10:00");
        formatStandardText(upperTimer,heightProperty(),30,Color.color(0.24, 0.24, 0.24),glowEffect(Color.CYAN,Color.MAGENTA));
        Text lowerTimer= new Text("10:00");
        formatStandardText(lowerTimer,heightProperty(),30,Color.color(0.24, 0.24, 0.24),glowEffect(Color.CYAN,Color.MAGENTA));
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
        rightMostPane.getChildren().add(muteButton);

        //pauseMenuButton
        CustomButton pauseMenuButton = new CustomButton(heightProperty().divide(7),"MenuIcon.png");
        pauseMenuButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        pauseMenuButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        rightMostPane.getChildren().add(pauseMenuButton);
        pauseMenuButton.setOnAction(e-> showPauseMenu());



        CustomButton resume= new CustomButton(heightProperty(),"RESUME",15,Color.WHITE);
        resume.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        resume.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

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

        pauseMenu.getChildren().addAll(resume,previousSceneButton,nextSceneButton);
        pauseMenu.minWidthProperty().bind(chessBoardPane.heightProperty().multiply(0.9));

    }

    private void startTimers(Text upperTimer,Text lowerTimer) {
        Date startTime= new Date();

        clockTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(chessBoardPane.internalBoard.isWhiteTurn()==whiteIsBottom){
                    whiteRemainingTime[0]-=1000;
                    long minutes=TimeUnit.MILLISECONDS.toMinutes(whiteRemainingTime[0]);
                    long millis =whiteRemainingTime[0]-TimeUnit.MINUTES.toMillis(minutes);//
                    Platform.runLater(()->lowerTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis))));
                }else{
                    blackRemainingTime[0]-=1000;
                    long minutes=TimeUnit.MILLISECONDS.toMinutes(blackRemainingTime[0]);
                    long millis =blackRemainingTime[0]-TimeUnit.MINUTES.toMillis(minutes);//
                    Platform.runLater(()->upperTimer.setText(String.format("%d:%02d", minutes,TimeUnit.MILLISECONDS.toSeconds(millis))));
                }
                if(blackRemainingTime[0]==0||whiteRemainingTime[0]==0){
                    cancel();
                    Platform.runLater(()->chessBoardPane.endGame(true));
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
            formatStandardText(endMessage,heightProperty(),15,Color.color(0.24, 0.24, 0.24),glowEffect(Color.RED,Color.GOLD));
        }else{
            endMessage= new Text("WHITE WON");
            formatStandardText(endMessage,heightProperty(),15,Color.WHITE,glowEffect(Color.CYAN,Color.GOLD));
        }
        if(!MoveGen.isInCheck(BitBoard.fromFEN(chessBoardPane.internalBoard.toFEN()))&&blackRemainingTime[0]!=0&&whiteRemainingTime[0]!=0){
            endMessage= new Text("DRAW");
            formatStandardText(endMessage,heightProperty(),15,Color.BROWN,glowEffect(Color.RED,Color.CYAN));
        }



        pauseMenu.getChildren().remove(0);//removes resume button
        pauseMenu.getChildren().add(0,endMessage);

        pauseMenu.spacingProperty().bind(heightProperty().divide(8));



        pauseMenu.getChildren().add(1,nextSceneButton2);
        mainPane.getChildren().removeAll(moveHistory,rightMostPane);
        mainPane.getChildren().add(pauseMenu);

    }

    @Override
    public GamePane previousMenu() {//Main menu
        return new MainMenuPane();
    }

    @Override
    public GamePane nextMenu() {//Exit game
        Platform.exit();
        System.exit(0);
        return null;
    }

    @Override
    public GamePane nextMenu2() {//rematch
        return null;//todo
    }
}
