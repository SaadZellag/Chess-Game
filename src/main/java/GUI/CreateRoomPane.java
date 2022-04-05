package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import server.Client;
import server.GameServer;

import static GUI.GUI.formatStandardText;
import static GUI.GUI.glowEffect;

public class CreateRoomPane extends GamePane {
    CreateRoomPane(){

        GameServer gameServer= new GameServer();
        gameServer.accept();


        Client serverClient= new Client("localhost");//todo change to  localhost

        VBox mainPane= new VBox();

        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(15).doubleValue()));
        getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10);

        final Text CREATE_ROOM= new Text(" CREATE ROOM");
        formatStandardText(CREATE_ROOM,heightProperty(),30);

        VBox topPane= new VBox(PLAY,CREATE_ROOM);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setSpacing(0);
        topPane.setPadding(new Insets(40,0,0,40));

        StackPane bottomPane= new StackPane();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);





        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");


        bottomPane.getChildren().add(previousSceneButton);

        mainPane.getChildren().addAll(topPane,nextSceneButton,bottomPane);
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