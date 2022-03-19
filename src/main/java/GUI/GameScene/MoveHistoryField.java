package GUI.GameScene;

import com.sun.javafx.tk.FontLoader;
import engine.internal.BitMove;
import game.ChessUtils;
import game.Move;
import game.Piece;
import game.PieceType;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.*;

import java.util.LinkedList;

import static GUI.GUI.getImage;
import static GUI.GUI.getResource;

public class MoveHistoryField extends VBox {
    private final ImageView backgroundImagePane = new ImageView(getImage("RoundTextArea.png"));
    private final ScrollPane scrollPane= new ScrollPane();
    private final ImageView textHeaderPane= new ImageView(getImage("PlaceHolderText.png"));
    private final StackPane stackPane= new StackPane();
    private final VBox textHolder= new VBox();
    private double fontSize=20;
    public ObservableList<Move>movesList;
    public MoveHistoryField(ObservableList<Move>movesList, ReadOnlyDoubleProperty binding){
        this.movesList=movesList;
        prefSizePropertyBind(binding);
        setAlignment(Pos.CENTER);
        getChildren().addAll(textHeaderPane,stackPane);

        stackPane.getChildren().addAll(backgroundImagePane,scrollPane);
        textHolder.setAlignment(Pos.CENTER);
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:transparent;");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(textHolder);

        scrollPane.setPannable(true);

        movesList.addListener((ListChangeListener<? super Move>) e->{
            textHolder.getChildren().clear();
            for (Move mv:movesList) {
                displayMove(mv);
            }
        });

        widthProperty().addListener(e->{
            fontSize=getWidth()/16.7;
            textHolder.getChildren().clear();
            for (Move mv:movesList) {
                displayMove(mv);
            }
        });

//        setOnMouseClicked(e->{//Todo this is for testing, remove once done
//            for (int i = 0; i < 100; i++) {
//                displayMove(new Move(new Piece(false, PieceType.PAWN),1,3));
//            }
//        });

    }
    private void displayMove(Move move){
        String pieceMoved="";

        if(move.piece.isWhite){
            switch (move.piece.type){
                case QUEEN ->pieceMoved="♕";
                case KING ->pieceMoved="♔";
                case ROOK ->pieceMoved="♖";
                case BISHOP ->pieceMoved="♗";
                case KNIGHT ->pieceMoved="♘";
                case PAWN ->pieceMoved="♙";
            }
        }else{
            switch (move.piece.type){
                case PAWN ->pieceMoved="♟";
                case KING ->pieceMoved="♚";
                case QUEEN ->pieceMoved="♛";
                case ROOK ->pieceMoved="♜";
                case BISHOP ->pieceMoved="♝";
                case KNIGHT ->pieceMoved="♞";
            }
        }

        Text piece= new Text(pieceMoved);
        piece.setFont(Font.font("", FontWeight.BOLD, fontSize));
        Text UCI= new Text(ChessUtils.moveToUCI(move));
        UCI.setFont(Font.loadFont(getResource("pixelatedFont.otf"),fontSize));
        TextFlow textFlow = new TextFlow();
        textFlow.getChildren().addAll(piece,UCI);
        textHolder.getChildren().add(textFlow);
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
