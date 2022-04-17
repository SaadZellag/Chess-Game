package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GameMode;
import GUI.GamePane;
import GUI.GameplayPanes.MultiplayerGamePane;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import server.Client;
import server.IdGenerator;

import java.util.ArrayList;
import java.util.HashMap;

import static GUI.GUI.*;
import static GUI.GameMode.ONLINE;

public class JoinRoomPane extends MenuPane {
    private final VBox LIST_OF_ROOMS= new VBox();
    private final Text NO_ROOMS_FOUND= new Text("NO ROOMS FOUND, PLEASE REFRESH");
    private final Text LOOKING= new Text("LOOKING FOR ROOMS...");
    private String hostIp;
    JoinRoomPane(){
        heightProperty().addListener(e->MIDDLE_PANE.setSpacing(heightProperty().divide(20).doubleValue()));

        UPPER_TEXT.setText("PLAY");
        UPPER_SUBTEXT.setText(" JOIN ROOM");
        nextSceneButton = new CustomButton(heightProperty(),"JOIN ROOM",10);
        nextSceneButton.setDisable(true);

        final ScrollPane SCROLL_PANE= new ScrollPane();
        SCROLL_PANE.setStyle("-fx-background:transparent;-fx-background-color:rgba(0,0,255,0.5);");
        SCROLL_PANE.prefHeightProperty().bind(heightProperty().divide(1.5));
        SCROLL_PANE.maxWidthProperty().bind(widthProperty().divide(1.3));
        SCROLL_PANE.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SCROLL_PANE.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SCROLL_PANE.setPannable(true);

        SCROLL_PANE.setContent(LIST_OF_ROOMS);
        LIST_OF_ROOMS.setAlignment(Pos.CENTER);
        LIST_OF_ROOMS.prefWidthProperty().bind(SCROLL_PANE.widthProperty());

        formatStandardText(NO_ROOMS_FOUND,heightProperty(),18);
        formatStandardText(LOOKING,heightProperty(),18);

        findOpenRooms();

        final Text REFRESH_PROMPT= new Text("PRESS R TO REFRESH THE ROOM LIST");
        formatStandardText(REFRESH_PROMPT,heightProperty(),23);
        setOnKeyPressed(e->{
            if(e.getCode()== KeyCode.R) {
                findOpenRooms();
            }
        });

        MIDDLE_PANE.getChildren().addAll(REFRESH_PROMPT,SCROLL_PANE, nextSceneButton);
    }

    private void findOpenRooms() {
        IdGenerator generator = new IdGenerator();
        LIST_OF_ROOMS.getChildren().clear();
        LIST_OF_ROOMS.getChildren().add(LOOKING);
        Thread t = new Thread(()->{
            ArrayList<String> ArrayList =Client.getHostIPs();

            Platform.runLater(()->{
                LIST_OF_ROOMS.getChildren().clear();
                if(ArrayList.size() == 0) {
                    LIST_OF_ROOMS.getChildren().add(NO_ROOMS_FOUND);
                    nextSceneButton.setDisable(true);
                }
                else{
                    for (String IP:ArrayList) {
                        CustomButton room= new CustomButton(heightProperty(), generator.ipToID(IP),20);
                        room.setOnAction(e->{
                            hostIp=IP;
                            nextSceneButton.setDisable(false);
                        });
                        LIST_OF_ROOMS.getChildren().add(room);
                    }
                }
            });
        });
        t.start();
    }

    @Override
    public GamePane nextMenu() {//Create room
        joinServer(hostIp);
        return new MultiplayerGamePane(false, GameMode.ONLINE);
    }


    @Override
    public GamePane previousMenu() {//PlayPane
        return new MultiplayerLAN_Pane();//todo need to close server when exiting
    }

}
