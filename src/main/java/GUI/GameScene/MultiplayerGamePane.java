package GUI.GameScene;

import game.Move;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import static GUI.GUI.*;

public class MultiplayerGamePane extends GamePane {
    public HBox mainPane = new HBox();
    public VBox settingsMenu;

    private final StackPane root = new StackPane();

    ObservableList<Move> moveHistoryList;
    public MoveHistoryField moveHistory;
    public boolean whiteIsBottom=true;
    public ChessBoardPane chessBoardPane;
    private final Text whiteWon= new Text("WHITE WON");
    private final Text blackWon = new Text("BLACK WON");
    private double fontSize=19;
    private Text upperTimer;
    private Text lowerTimer;
    private final VBox rightMostPane;
    private final VBox leftMostPane;

    public MultiplayerGamePane() {
        whiteWon.setEffect(new ColorAdjust(1,1,1,0));
        blackWon.setEffect(new ColorAdjust(1,1,0,1));//todo remove this in final version


        //parent inherited buttons
        muteButton= new ImageCustomButton(heightProperty().divide(11),"Board.png");

        previousSceneButton = new TextCustomButton(heightProperty(),"MAIN MENU",15,Color.GREY);
        previousSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        previousSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        nextSceneButton= new TextCustomButton(heightProperty(),"QUIT GAME",15,Color.GREY);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);


        getChildren().add(root);
        root.getChildren().add(mainPane);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(5,20,5,20));


        //lefMostPane
        leftMostPane= new VBox();


        chessBoardPane = new ChessBoardPane(heightProperty(), this::endGame);
        if (!whiteIsBottom){
            chessBoardPane.setRotate(180);
            chessBoardPane.rotatePieces();
        }
        upperTimer= new Text("10:00");
        formatStandardText(upperTimer,heightProperty(),30,Color.color(0.24, 0.24, 0.24),glowEffect(Color.CYAN,Color.MAGENTA));
        lowerTimer= new Text("10:00");
        formatStandardText(lowerTimer,heightProperty(),30,Color.color(0.24, 0.24, 0.24),glowEffect(Color.CYAN,Color.MAGENTA));
        lowerTimer.setViewOrder(1);

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

        //settingsButton
        ImageCustomButton settingsButton = new ImageCustomButton(heightProperty().divide(7),"Board.png");
        rightMostPane.getChildren().add(settingsButton);
        settingsButton.setOnAction(e-> showSettingsMenu());



        TextCustomButton resume= new TextCustomButton(heightProperty(),"RESUME",15,Color.GREY);
        resume.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        resume.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        resume.setOnAction(e->{//remove settings menu
            mainPane.setEffect(null);
            mainPane.setDisable(false);
            root.getChildren().remove(settingsMenu);
        });

        //settings menu
        settingsMenu= new VBox();
        settingsMenu.setAlignment(Pos.CENTER);
        settingsMenu.spacingProperty().bind(heightProperty().divide(6));
        settingsMenu.setBackground(getBackgroundImage("RoundTextArea.png",settingsMenu,false));
        settingsMenu.getChildren().addAll(resume,previousSceneButton,nextSceneButton);



    }

    private void showSettingsMenu(){
        mainPane.setDisable(true);
        mainPane.setEffect(new GaussianBlur(30));
        root.getChildren().add(settingsMenu);
    }

    private void endGame(){//todo give option to ask for a rematch
        Text checkmate= chessBoardPane.internalBoard.isWhiteTurn()?blackWon:whiteWon;
        formatStandardText(checkmate,heightProperty(),15,Color.GREY,glowEffect(Color.RED,Color.GOLD));
//        checkmate.setPreserveRatio(true);
//        checkmate.fitWidthProperty().bind(heightProperty().divide(3));
        settingsMenu.getChildren().remove(0);//removes resume button
        settingsMenu.getChildren().add(0,checkmate);

        settingsMenu.spacingProperty().bind(heightProperty().divide(8));

        settingsMenu.getChildren().add(1,new ImageCustomButton(
                heightProperty().divide(10),
                "PlaceHolderText.png"
        ));
        mainPane.getChildren().removeAll(moveHistory,rightMostPane);
        mainPane.getChildren().add(settingsMenu);

    }

    @Override
    public GamePane previousMenu() {
        return new SingleplayerGamePane();
    }

    @Override
    public GamePane nextMenu() {
        Platform.exit();
        System.exit(0);
        return null;
    }
}
