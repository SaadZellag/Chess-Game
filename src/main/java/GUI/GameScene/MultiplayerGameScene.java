package GUI.GameScene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MultiplayerGameScene extends GameScene {
    HBox mainPane = new HBox();
    StackPane root;
    MoveHistoryField moveHistory = new MoveHistoryField(heightProperty());

    Image backgroundImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Main Background.png"));
    BackgroundImage bImage = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1000, 1000, true, true, true, true));
    Background backGround = new Background(bImage);

    final Image PAWN= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image KNIGHT= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image BISHOP= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image QUEEN= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image KING= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));

    boolean whiteIsBottom=true;

    public MultiplayerGameScene(double width, double height) {
        super(new StackPane(), width, height);
        //parent inherited buttons
        muteButton= new MuteButton(heightProperty());
        previousSceneButton = new Button("Resign");
        nextSceneButton= new Button("Quit Game");

        root = (StackPane) this.getRoot();
        root.getChildren().add(mainPane);
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
        mainPane.getChildren().add(moveHistory);

        //rightMostPane
        VBox rightMostPane= new VBox();
        mainPane.getChildren().add(rightMostPane);
        rightMostPane.setAlignment(Pos.BOTTOM_RIGHT);
        rightMostPane.setSpacing(10);

        //muteButton
        rightMostPane.getChildren().add(muteButton);


        //settingsButton
        SettingsButton settingsButton= new SettingsButton(heightProperty());
        rightMostPane.getChildren().add(settingsButton);
        settingsButton.setOnAction(e->settingsMenu());


        //Testing stuff
        StackPane[] tileStacks= chessBoardPane.tileStacks;
        for (StackPane tile: tileStacks) {
            ImageView p= new ImageView(PAWN);
            p.setPreserveRatio(true);
            p.setMouseTransparent(true);
            p.fitWidthProperty().bind(tile.widthProperty());
            tile.getChildren().add(p);
        }




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
        ImageView resumeGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg")));
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
        ImageView resignGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg")));
        resignGraphic.fitHeightProperty().bind(heightProperty().divide(10));
        resignGraphic.setPreserveRatio(true);
        previousSceneButton.setGraphic(resignGraphic);
        previousSceneButton.prefHeightProperty().bind(heightProperty().divide(10));
        previousSceneButton.setBackground(null);

        //quitButton is nextSceneButton
        ImageView quitGraphic=new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg")));
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
    public GameScene previousScene() {
        return new SingleplayerGameScene(getWidth(),getHeight());
    }

    @Override
    public GameScene nextScene() {
        Platform.exit();
        System.exit(0);
        return null;
    }
}
