package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.Client;

import static GUI.GUI.*;

public class JoinRoomPane extends GamePane{
    JoinRoomPane(){
//        Client connectingClient= new Client("localhost");//todo write ip of the host, only create once host room has been selected


        //todo client needs to be in a loop



        VBox mainPane= new VBox();

        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(15).doubleValue()));
        getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10, Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));

        final Text LAN_PLAY= new Text(" JOIN ROOM");
        formatStandardText(LAN_PLAY,heightProperty(),30,Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));

        VBox topPane= new VBox(PLAY,LAN_PLAY);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setSpacing(0);
        topPane.setPadding(new Insets(40,0,0,40));

        StackPane bottomPane= new StackPane();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);


        nextSceneButton = new CustomButton(heightProperty(),"JOIN ROOM",10, Color.WHITE);
        nextSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        nextSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);
        nextSceneButton.setOnAction(e-> this.nextMenu());
        nextSceneButton.setDisable(true);

        final VBox listOfRooms= new VBox();
        final ScrollPane scrollPane= new ScrollPane();
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:rgba(0,0,255,0.5);");
//        final ImageView backgroundImagePane = new ImageView(getImage("RoundTextArea.png"));
        listOfRooms.setAlignment(Pos.CENTER);

//
        scrollPane.prefHeightProperty().bind(heightProperty().divide(3));
        scrollPane.prefWidthProperty().bind(widthProperty().divide(2));


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(listOfRooms);
        scrollPane.setPannable(true);

        final TextFlow NO_ROOMS_FOUND= new TextFlow(new Text("NO ROOMS FOUND, PLEASE REFRESH"));
        listOfRooms.getChildren().add(NO_ROOMS_FOUND);

        formatStandardText((Text)NO_ROOMS_FOUND.getChildren().get(0),heightProperty(),10, Color.color(0.24,0.24,0.24),glowEffect(Color.CYAN,Color.MAGENTA));


        ToggleGroup toggleGroup= new ToggleGroup();

        //todo create refresh button


        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");
        previousSceneButton.setIdleGlowEffect(Color.CYAN,Color.MAGENTA);
        previousSceneButton.setHoveredGlowEffect(Color.RED,Color.TRANSPARENT);

        bottomPane.getChildren().add(previousSceneButton);

        mainPane.getChildren().addAll(topPane,scrollPane,nextSceneButton,bottomPane);
    }
    @Override
    public GamePane nextMenu() {//Create room
        return new MultiplayerGamePane(true,false);//todo
    }


    @Override
    public GamePane previousMenu() {//PlayPane
        return new PlayPane();//todo need to close server when exiting
    }

}
