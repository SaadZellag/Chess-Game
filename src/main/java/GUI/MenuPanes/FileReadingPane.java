package GUI.MenuPanes;

import GUI.GUI;
import GUI.GamePane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import server.GameServer;

import java.io.*;
import java.util.Objects;

import static GUI.GUI.*;

public class FileReadingPane extends MenuPane{
    public FileReadingPane(String subtext, String fileName){
        final ScrollPane SCROLL_PANE= new ScrollPane();
        SCROLL_PANE.setStyle("-fx-background:transparent;-fx-background-color:rgba(1,1,1,0.2);");
        SCROLL_PANE.prefHeightProperty().bind(heightProperty().divide(1.3));
        SCROLL_PANE.maxWidthProperty().bind(widthProperty().divide(1.1));
        SCROLL_PANE.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SCROLL_PANE.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        SCROLL_PANE.setPannable(true);

        final VBox TEXT_HOLDER= new VBox();
        SCROLL_PANE.setContent(TEXT_HOLDER);
        TEXT_HOLDER.setAlignment(Pos.TOP_LEFT);
        TEXT_HOLDER.prefWidthProperty().bind(SCROLL_PANE.widthProperty().multiply(0.99));
        TEXT_HOLDER.setSpacing(15);

        try (BufferedReader reader = new BufferedReader (new InputStreamReader(GameServer.getResourceStream("GUI/" + fileName)))) {
            String line;
            int HEADER_SCALE_1 = 15;
            int HEADER_SCALE_2 = 20;
            int HEADER_SCALE_3 = 25;
            int PARAGRAPH_SCALE = 30;
            while((line=reader.readLine())!=null){
                String [] split=line.split("[|]");
                Label text= new Label(split[1]);
                if(split[0].contains("h")){//Is a header
                    switch (split[0].charAt(split[0].length()-1)){
                        case '1'->formatTipsText(text,heightProperty(), HEADER_SCALE_1);
                        case '2'->formatTipsText(text,heightProperty(), HEADER_SCALE_2);
                        case '3'->formatTipsText(text,heightProperty(), HEADER_SCALE_3);
                    }
                }else{
                    formatTipsText(text,heightProperty(), PARAGRAPH_SCALE);
                }
//                text.setEffect(glowEffect(Color.BLACK,Color.BLACK));
                text.setTextAlignment(TextAlignment.LEFT);
                TEXT_HOLDER.getChildren().add(text);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        UPPER_TEXT.setText("TIPS");
        UPPER_SUBTEXT.setText("  "+subtext);
        MIDDLE_PANE.getChildren().add(SCROLL_PANE);
    }
    @Override
    public GamePane previousMenu(){
        return new TipsPane();
    }
}
