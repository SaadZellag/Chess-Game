package GUI.GameScene;

import game.Move;
import game.Piece;
import game.PieceType;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;

public class MoveHistoryField extends VBox {
    Image backgroundImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/RoundTextArea.png"));
    ImageView backgroundImagePane = new ImageView(backgroundImage);
    ScrollPane scrollPane= new ScrollPane();
    Image textHeader = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/PlaceHolderText.png"));
    ImageView textHeaderPane= new ImageView(textHeader);
    StackPane stackPane= new StackPane();
    VBox textHolder= new VBox();
    public MoveHistoryField(ReadOnlyDoubleProperty binding){
        prefSizePropertyBind(binding);
        setAlignment(Pos.CENTER);
        getChildren().addAll(textHeaderPane,stackPane);

        stackPane.getChildren().addAll(backgroundImagePane,scrollPane);
        textHolder.setPadding(new Insets(0,30,0,30));
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:transparent;");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(textHolder);

        //text testing
        for (int i = 0; i < 100; i++) {
            displayMove(new Move(new Piece(false, PieceType.PAWN),1,3));
        }

    }
    private void displayMove(Move move){
        Text t= new Text(move.piece.type.toString()+move.initialLocation+move.finalLocation+"♕♔♖♗♘♙♚♛♜♝♞♟");
        t.setFont(Font.font("Verdana", FontWeight.BOLD, 13));
        textHolder.getChildren().add(t);
    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        backgroundImagePane.setPreserveRatio(true);
        backgroundImagePane.fitHeightProperty().bind(binding.divide(1.1));

        textHeaderPane.setPreserveRatio(true);
        textHeaderPane.fitWidthProperty().bind(binding.divide(3));

        scrollPane.maxHeightProperty().bind(binding.divide(1.145*1.1));
        scrollPane.maxWidthProperty().bind(binding.divide(1.637*1.1));
    }

    public void prefSizePropertyBind (DoubleBinding binding){
        backgroundImagePane.setPreserveRatio(true);
        backgroundImagePane.fitHeightProperty().bind(binding.divide(1.1));

        textHeaderPane.setPreserveRatio(true);
        textHeaderPane.fitWidthProperty().bind(binding.divide(3));

        scrollPane.maxHeightProperty().bind(binding.divide(1.145*1.1));
        scrollPane.maxWidthProperty().bind(binding.divide(1.637*1.1));
    }
}
