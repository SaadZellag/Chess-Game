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
//        mainPane.setSpacing(0);
        mainPane.setPadding(new Insets(20,20,20,20));

        //moveHistory
        MoveHistoryField moveHistory = new MoveHistoryField(heightProperty());
        mainPane.getChildren().add(moveHistory);

        //rightMostPane properties
        VBox rightMostPane= new VBox();
        mainPane.getChildren().add(rightMostPane);
        rightMostPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightMostPane.setSpacing(10);

        //muteButton
        MuteButton muteButton= new MuteButton();
        muteButton.prefSizePropertyBind(chessBoardPane.widthProperty().divide(7));
        rightMostPane.getChildren().add(muteButton);

        //settingsButton
        SettingsButton settingsButton= new SettingsButton();
        settingsButton.prefSizePropertyBind(chessBoardPane.widthProperty().divide(7));
        rightMostPane.getChildren().add(settingsButton);


        //bs
        Image textHeader = new Image("https://placeholder.com/wp-content/uploads/2018/10/placeholder-1.png");
        ImageView textHeaderPane= new ImageView(textHeader);


    }

}
