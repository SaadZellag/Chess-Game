package GUI.GameScene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MultiplayerGameScene extends Scene {

    HBox mainPane;
    Image backgroundImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Main Background.png"));
    BackgroundImage bImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1000, 1000, true, true, true, true));
    Background backGround = new Background(bImage);
    boolean whiteIsBottom=true;

    public MultiplayerGameScene(double width, double height) {
        super(new HBox(), width, height);
        mainPane = (HBox) this.getRoot();
        mainPane.setSpacing(20);
        mainPane.setAlignment(Pos.CENTER_LEFT);
        mainPane.setPadding(new Insets(5,20,20,20));
        mainPane.setBackground(backGround);


        //lefMostPane
        VBox leftMostPane= new VBox();
        Text upperTimer= new Text("10:00");
        upperTimer.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,17));
        upperTimer.setFill(Color.WHITE);
        Text lowerTimer= new Text("10:00");
        lowerTimer.setFill(Color.WHITE);
        lowerTimer.setFont(Font.font("Verdana", FontWeight.BOLD,FontPosture.ITALIC, 17));
        ChessBoardPane chessBoardPane = new ChessBoardPane(heightProperty());
        leftMostPane.getChildren().addAll(upperTimer,chessBoardPane,lowerTimer);
        leftMostPane.setAlignment(Pos.CENTER_LEFT);
        mainPane.getChildren().add(leftMostPane);

        //moveHistory
        MoveHistoryField moveHistory = new MoveHistoryField(heightProperty());
        mainPane.getChildren().add(moveHistory);

        //rightMostPane
        VBox rightMostPane= new VBox();
        mainPane.getChildren().add(rightMostPane);
        rightMostPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightMostPane.setSpacing(10);

        //muteButton
        MuteButton muteButton= new MuteButton(heightProperty());
        rightMostPane.getChildren().add(muteButton);

        //settingsButton
        SettingsButton settingsButton= new SettingsButton(heightProperty());
        rightMostPane.getChildren().add(settingsButton);

    }

}
