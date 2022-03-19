package GUI.GameScene;

import game.Move;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.LinkedList;

import static GUI.GUI.getBackgroundImage;
import static GUI.GUI.getImage;

public class MultiplayerGamePane extends GamePane {
    public HBox mainPane = new HBox();
    public VBox settingsMenu;

    private final StackPane root = new StackPane();

    LinkedList<Move>moveHistoryList;
    public MoveHistoryField moveHistory;
    public boolean whiteIsBottom=true;
    public ChessBoardPane chessBoardPane;
    private final ImageView whiteWon= new ImageView(getImage("PlaceHolderText.png"));
    private final ImageView blackWon = new ImageView(getImage("PlaceHolderText.png"));

    public MultiplayerGamePane() {
        whiteWon.setEffect(new ColorAdjust(1,1,1,0));
        blackWon.setEffect(new ColorAdjust(1,1,0,1));//todo remove this in final version


        //parent inherited buttons
        muteButton= new CustomButton(heightProperty().divide(11),heightProperty().divide(11),"Board.png");

        previousSceneButton = new CustomButton(heightProperty().divide(10),heightProperty().divide(2),"PlaceHolderText.png");

        nextSceneButton= new CustomButton(heightProperty().divide(10),heightProperty().divide(2),"PlaceHolderText.png");


        getChildren().add(root);
        root.getChildren().add(mainPane);
        mainPane.setSpacing(10);
        mainPane.setPadding(new Insets(5,20,5,20));


        //lefMostPane
        VBox leftMostPane= new VBox();
        Text upperTimer= new Text("10:00");
        upperTimer.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,17));
        upperTimer.setFill(Color.WHITE);
        Text lowerTimer= new Text("10:00");
        lowerTimer.setViewOrder(1);
        lowerTimer.setFill(Color.WHITE);
        lowerTimer.setFont(Font.font("Verdana", FontWeight.BOLD,FontPosture.ITALIC, 17));
        chessBoardPane = new ChessBoardPane(heightProperty(), this::endGame);
        if (!whiteIsBottom){
            chessBoardPane.setRotate(180);
            chessBoardPane.rotatePieces();
        }
        leftMostPane.getChildren().addAll(upperTimer,chessBoardPane,lowerTimer);
        leftMostPane.setAlignment(Pos.CENTER_LEFT);
        mainPane.getChildren().add(leftMostPane);

        //moveHistory
        moveHistoryList=chessBoardPane.moveHistoryList;
        moveHistory= new MoveHistoryField(moveHistoryList,heightProperty());
        mainPane.getChildren().add(moveHistory);
        moveHistory.setViewOrder(1);

        //rightMostPane
        VBox rightMostPane= new VBox();
        mainPane.getChildren().add(rightMostPane);
        rightMostPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightMostPane.setSpacing(10);
        rightMostPane.setViewOrder(1);

        //muteButton
        rightMostPane.getChildren().add(muteButton);

        //settingsButton
        CustomButton settingsButton = new CustomButton(heightProperty().divide(7),heightProperty().divide(7),"Board.png");
        rightMostPane.getChildren().add(settingsButton);
        settingsButton.setOnAction(e-> showSettingsMenu());



        CustomButton resume = new CustomButton(
                heightProperty().divide(10),
                heightProperty().divide(2),
                "PlaceHolderText.png"
        );
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
        ImageView checkmate= chessBoardPane.internalBoard.isWhiteTurn()?blackWon:whiteWon;
        checkmate.setPreserveRatio(true);
        checkmate.fitWidthProperty().bind(heightProperty().divide(3));
        settingsMenu.getChildren().remove(0);//removes resume button
        settingsMenu.getChildren().add(0,checkmate);

        settingsMenu.spacingProperty().bind(heightProperty().divide(8));

        settingsMenu.getChildren().add(1,new CustomButton(
                heightProperty().divide(10),
                heightProperty().divide(2),
                "PlaceHolderText.png"
        ));
        showSettingsMenu();

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
