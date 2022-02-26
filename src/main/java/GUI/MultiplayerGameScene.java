package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MultiplayerGameScene extends Scene {

    private HBox mainPane;


    public MultiplayerGameScene(double width, double height) {
        super(new HBox(), width, height);
        mainPane = (HBox) this.getRoot();
        ChessBoardPane chessBoardPane = new ChessBoardPane(heightProperty().divide(1.1));


        //mainPane properties
        mainPane.getChildren().add(chessBoardPane);
        mainPane.setSpacing(20);
        mainPane.setPadding(new Insets(20,20,20,20));

        //rightPane properties
        VBox rightPane= new VBox();
        mainPane.getChildren().add(rightPane);
        rightPane.setAlignment(Pos.CENTER);

        //moveHistory
        MoveHistoryField moveHistory = new MoveHistoryField(chessBoardPane.heightProperty());
        rightPane.getChildren().add(moveHistory);

        //lowerRightPane
        HBox lowerRightPane= new HBox();
        lowerRightPane.setSpacing(10);
        lowerRightPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightPane.getChildren().add(lowerRightPane);


        //muteButton
        MuteButton muteButton= new MuteButton();
        muteButton.prefSizePropertyBind(heightProperty().divide(7));
        lowerRightPane.getChildren().add(muteButton);

        //settingsButton
        SettingsButton settingsButton= new SettingsButton();
        settingsButton.prefSizePropertyBind(heightProperty().divide(7));
        lowerRightPane.getChildren().add(settingsButton);


    }

}
