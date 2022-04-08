package GUI.GameplayPanes;

import game.ChessUtils;
import game.Move;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import static GUI.GUI.*;

public class MoveHistoryField extends VBox {
    private final ImageView backgroundImagePane = new ImageView(getImage("RoundTextArea.png"));
    private final ScrollPane scrollPane= new ScrollPane();
    private final Text textHeader= new Text("MOVE HISTORY");
    private final VBox textHolder= new VBox();
    private double fontSize=20;
    public ObservableList<Move>movesList;
    public MoveHistoryField(ObservableList<Move>movesList, ReadOnlyDoubleProperty binding){
        this.movesList=movesList;
        prefSizePropertyBind(binding);
        setAlignment(Pos.CENTER);
        StackPane stackPane = new StackPane();
        getChildren().addAll(textHeader, stackPane);

        stackPane.getChildren().addAll(backgroundImagePane,scrollPane);
        textHolder.setAlignment(Pos.CENTER);
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:transparent;");

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setContent(textHolder);

        scrollPane.setPannable(true);

        movesList.addListener((ListChangeListener<? super Move>) e->{
            displayMoves();
            scrollPane.layout();
            scrollPane.setVvalue(200);
        });

        textHeader.setFill(Color.color(0.24, 0.24, 0.24));
        textHeader.setFont(Font.loadFont(getResource("standardFont.ttf"),fontSize*1.3));
        textHeader.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));

        widthProperty().addListener(e->{
            fontSize=getWidth()/16.7;
            textHeader.setFont(Font.loadFont(getResource("standardFont.ttf"),fontSize*1.3));
            displayMoves();

        });
    }
    private void displayMoves(){
        textHolder.getChildren().clear();
        for (Move move:movesList) {
            boolean isWhite=move.piece.isWhite;

            String pieceMoved=switch (move.piece.type){
                case QUEEN ->isWhite?"♕":"♛";
                case KING ->isWhite?"♔":"♚";
                case ROOK ->isWhite?"♖":"♜";
                case BISHOP ->isWhite?"♗":"♝";
                case KNIGHT ->isWhite?"♘":"♞";
                case PAWN ->isWhite?"♙":"♟";
            };
            String promotion="";
            if(move.promotionPiece!=null)
                promotion=switch (move.promotionPiece){
                    case QUEEN ->isWhite?"->♕":"->♛";
                    case KING ->isWhite?"->♔":"->♚";
                    case ROOK ->isWhite?"->♖":"->♜";
                    case BISHOP ->isWhite?"->♗":"->♝";
                    case KNIGHT ->isWhite?"->♘":"->♞";
                    case PAWN ->isWhite?"->♙":"->♟";
                };


            Text piece= new Text(pieceMoved);
            piece.setFont(Font.font("", FontWeight.BOLD, fontSize));

            String moveStr = ChessUtils.moveToUCI(move);

            Text UCI= new Text(moveStr.substring(0, 2) + "-" + moveStr.substring(2, 4));
            UCI.setFont(Font.loadFont(getResource("pixelatedFont.otf"),fontSize));

            Text promotionPiece= new Text(promotion);
            promotionPiece.setFont(Font.font("", FontWeight.BOLD, fontSize));

            TextFlow textFlow = new TextFlow();
            textFlow.getChildren().addAll(piece,UCI,promotionPiece);
            textFlow.setEffect(glowEffect(Color.CYAN,Color.GREEN));
            textHolder.getChildren().add(textFlow);
        }
    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        backgroundImagePane.setPreserveRatio(true);
        backgroundImagePane.fitHeightProperty().bind(binding.divide(1.1));
//
//        textHeaderPane.setPreserveRatio(true);
//        textHeaderPane.fitWidthProperty().bind(binding.divide(3));

        scrollPane.maxHeightProperty().bind(binding.divide(1.145*1.1));
        scrollPane.maxWidthProperty().bind(binding.divide(1.637*1.1));

    }

    public void prefSizePropertyBind (DoubleBinding binding){
        backgroundImagePane.setPreserveRatio(true);
        backgroundImagePane.fitHeightProperty().bind(binding.divide(1.1));

//        textHeaderPane.setPreserveRatio(true);
//        textHeaderPane.fitWidthProperty().bind(binding.divide(3));

        scrollPane.maxHeightProperty().bind(binding.divide(1.145*1.1));
        scrollPane.maxWidthProperty().bind(binding.divide(1.637*1.1));
    }
}
