package GUI.GameScene;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MultiplayerGamePane extends GamePane {
    HBox mainPane = new HBox();
    StackPane root = new StackPane();
    MoveHistoryField moveHistory = new MoveHistoryField(heightProperty());
    boolean whiteIsBottom=false;
    public ChessBoardPane chessBoardPane;

    public MultiplayerGamePane() {

        //parent inherited buttons
        muteButton= new MuteButton(heightProperty());
        previousSceneButton = new Button("Resign");
        nextSceneButton= new Button("Quit Game");
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
        chessBoardPane = new ChessBoardPane(heightProperty());
        if (!whiteIsBottom){
            chessBoardPane.setRotate(180);
            chessBoardPane.rotatePieces();
        }
        leftMostPane.getChildren().addAll(upperTimer,chessBoardPane,lowerTimer);
        leftMostPane.setAlignment(Pos.CENTER_LEFT);
        mainPane.getChildren().add(leftMostPane);

        //moveHistory
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
        SettingsButton settingsButton= new SettingsButton(heightProperty());
        rightMostPane.getChildren().add(settingsButton);
        settingsButton.setOnAction(e->settingsMenu());



    }
    void settingsMenu(){
        Image settingsImage= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/RoundTextArea.png"));
        BackgroundImage bImage = new BackgroundImage(settingsImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(getWidth(), getHeight(), true, true, true, false));
        Background backGround = new Background(bImage);

        VBox settingsMenu= new VBox();
        settingsMenu.setAlignment(Pos.CENTER);
        settingsMenu.spacingProperty().bind(heightProperty().divide(6));
        settingsMenu.setBackground(backGround);


        Button resume = new Button("Resume");
        ImageView resumeGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png")));
        resumeGraphic.fitHeightProperty().bind(heightProperty().divide(10));
        resumeGraphic.setPreserveRatio(true);
        resume.setGraphic(resumeGraphic);
        resume.prefHeightProperty().bind(heightProperty().divide(10));
        resume.setBackground(null);
        resume.setOnAction(e->{
            mainPane.setEffect(null);
            mainPane.setDisable(false);
            root.getChildren().remove(settingsMenu);
        });

        //resignButton is previousSceneButton
        ImageView resignGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png")));
        resignGraphic.fitHeightProperty().bind(heightProperty().divide(10));
        resignGraphic.setPreserveRatio(true);
        previousSceneButton.setGraphic(resignGraphic);
        previousSceneButton.prefHeightProperty().bind(heightProperty().divide(10));
        previousSceneButton.setBackground(null);

        //quitButton is nextSceneButton
        ImageView quitGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png")));
        quitGraphic.fitHeightProperty().bind(heightProperty().divide(10));
        quitGraphic.setPreserveRatio(true);
        nextSceneButton.setGraphic(quitGraphic);
        nextSceneButton.prefHeightProperty().bind(heightProperty().divide(10));
        nextSceneButton.setBackground(null);

        settingsMenu.getChildren().addAll(resume,previousSceneButton,nextSceneButton);

        mainPane.setDisable(true);
        mainPane.setEffect(new GaussianBlur(30));
        root.getChildren().add(settingsMenu);
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
