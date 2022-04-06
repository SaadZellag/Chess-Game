package GUI;

import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Popup;
import javafx.stage.Stage;
import server.Client;

import java.awt.event.KeyEvent;
import java.security.cert.CertificateNotYetValidException;
import java.util.Collection;
import java.util.HashMap;

import static GUI.GUI.*;

public class JoinRoomPane extends GamePane{
    final VBox listOfRooms= new VBox();
    final Text NO_ROOMS_FOUND= new Text("NO ROOMS FOUND, PLEASE REFRESH");
    private String hostIp;
    CustomButton joinRoomButton = new CustomButton(heightProperty(),"JOIN ROOM",10);
    JoinRoomPane(){
//        Client connectingClient= new Client("localhost");//todo write ip of the host, only create once host room has been selected

        //todo client needs to be in a loop
        VBox mainPane= new VBox();

        mainPane.prefWidthProperty().bind(widthProperty());
        mainPane.prefHeightProperty().bind(heightProperty());
        mainPane.setAlignment(Pos.TOP_CENTER);
        heightProperty().addListener(e->mainPane.setSpacing(heightProperty().divide(20).doubleValue()));
        getChildren().add(mainPane);

        final Text PLAY= new Text("PLAY");
        formatStandardText(PLAY,heightProperty(),10);

        final Text LAN_PLAY= new Text(" JOIN ROOM");
        formatStandardText(LAN_PLAY,heightProperty(),30);

        VBox topPane= new VBox(PLAY,LAN_PLAY);
        topPane.setAlignment(Pos.TOP_LEFT);
        topPane.setSpacing(0);
        topPane.setPadding(new Insets(40,0,0,40));

        StackPane bottomPane= new StackPane();
        bottomPane.setAlignment(Pos.BOTTOM_LEFT);
        bottomPane.setPadding(new Insets(0,0,5,40));
        VBox.setVgrow(bottomPane, Priority.ALWAYS);


        joinRoomButton.setDisable(true);
        joinRoomButton.setOnAction(e-> {
            System.out.println("IP: "+hostIp);
        });


        final ScrollPane scrollPane= new ScrollPane();
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:rgba(0,0,255,0.5);");
        listOfRooms.setAlignment(Pos.CENTER);


//
        scrollPane.prefHeightProperty().bind(heightProperty().divide(1.5));
        scrollPane.maxWidthProperty().bind(widthProperty().divide(1.3));


        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(listOfRooms);
        listOfRooms.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setPannable(true);

        formatStandardText(NO_ROOMS_FOUND,heightProperty(),18);

        final Text REFRESH_PROMPT= new Text("PRESS R TO REFRESH THE ROOM LIST");
        formatStandardText(REFRESH_PROMPT,heightProperty(),23);






        previousSceneButton = new CustomButton(heightProperty().divide(10),"Board.png");
        bottomPane.getChildren().add(previousSceneButton);

        mainPane.getChildren().addAll(topPane,REFRESH_PROMPT,scrollPane,joinRoomButton,bottomPane);

        setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.R)
                findOpenRooms();
        });

        findOpenRooms();
    }

    private void findOpenRooms() {
        HashMap<String,String> hashMap=Client.getHostDict();
        String[] keys= hashMap.keySet().toArray(new String[0]);
        listOfRooms.getChildren().clear();
        if(keys.length==0) {
            listOfRooms.getChildren().add(NO_ROOMS_FOUND);
            joinRoomButton.setDisable(true);
        }
        else{
            for (String IP:keys) {
                for (int i=0;i<10;i++){
                CustomButton room= new CustomButton(heightProperty(),hashMap.get(IP),20);

                room.setOnAction(e->{
                    hostIp=IP;
                    joinRoomButton.setDisable(false);
                });
                listOfRooms.getChildren().add(room);}
            }
        }


    }

    @Override
    public GamePane nextMenu() {//Create room
        return new MultiplayerGamePane(true,false);//todo
    }


    @Override
    public GamePane previousMenu() {//PlayPane
        return new MultiplayerLAN_Pane();//todo need to close server when exiting
    }

}
