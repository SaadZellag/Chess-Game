package GUI.MenuPanes;

import engine.internal.BitBoard;
import game.Board;
import game.Piece;
import javafx.beans.binding.DoubleBinding;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.util.Objects;

import static GUI.GUI.*;
import static GUI.GUI.getImage;
public class TipsChessBoardPane extends StackPane {
    public Board internalBoard;
    private GridPane grid;
    public final ToggleButton[] buttons = new ToggleButton[64];
    private final String SELECTED_COLOR = "rgba(0, 255, 0, 0.5)";
    private final String UNSELECTED_COLOR = "transparent";
    private final Image W_PAWN=getImage("Pieces/W_Pawn.png");
    private final Image W_ROOK=getImage("Pieces/W_Rook.png");
    private final Image W_KNIGHT=getImage("Pieces/W_Knight.png");
    private final Image W_BISHOP=getImage("Pieces/W_Bishop.png");
    private final Image W_QUEEN=getImage("Pieces/W_Queen.png");
    private final Image W_KING=getImage("Pieces/W_King.png");
    private final Image B_PAWN=getImage("Pieces/B_Pawn.png");
    private final Image B_ROOK=getImage("Pieces/B_Rook.png");
    private final Image B_KNIGHT=getImage("Pieces/B_Knight.png");
    private final Image B_BISHOP=getImage("Pieces/B_Bishop.png");
    private final Image B_QUEEN=getImage("Pieces/B_Queen.png");
    private final Image B_KING=getImage("Pieces/B_King.png");

    public TipsChessBoardPane(DoubleBinding binding){//for the tips menu
        internalBoard=new Board(BitBoard.STARTING_POSITION_FEN);

        try {
            grid= FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUI/Panes/BaseChessBoardPane.fxml")));
            getChildren().add(grid);
            grid.setAlignment(Pos.CENTER);
            Background normalBackGround = getBackgroundImage("Board.png", this, true);
            setBackground(normalBackGround);

            //Create chess board
            for (int i=0;i<8;i++){
                for (int j=0;j<8;j++){
                    int index= i+8*j;

                    //ButtonTile properties
                    buttons[index]=new ToggleButton();
                    buttons[index].setPrefHeight(Double.MAX_VALUE);
                    buttons[index].setPrefWidth(Double.MAX_VALUE);
                    buttons[index].setStyle("-fx-background-color:"+ UNSELECTED_COLOR);
                    buttons[index].setPadding(Insets.EMPTY);

                    grid.add(buttons[index],i,j);

                    buttons[index].selectedProperty().addListener(e->{
                        if(buttons[index].isSelected())
                            buttons[index].setStyle("-fx-background-color:"+ SELECTED_COLOR);
                        else
                            buttons[index].setStyle("-fx-background-color:"+ UNSELECTED_COLOR);
                    });

                }
            }
            placePieces();
            prefSizePropertyBind(binding);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void placePieces() {
        Piece[] pieces = internalBoard.getPieces();

        for (int i = 0; i < pieces.length; i++) {
            if (pieces[i] == null) {
                buttons[i].setGraphic(null);
                buttons[i].setDisable(true);
                continue;
            }
            Piece p = pieces[i];
            Image image = switch (p.type) {
                case PAWN -> p.isWhite ? W_PAWN : B_PAWN;
                case BISHOP -> p.isWhite ? W_BISHOP : B_BISHOP;
                case KNIGHT -> p.isWhite ? W_KNIGHT : B_KNIGHT;
                case ROOK -> p.isWhite ? W_ROOK : B_ROOK;
                case QUEEN -> p.isWhite ? W_QUEEN : B_QUEEN;
                case KING -> p.isWhite ? W_KING : B_KING;
            };

            ImageView individualPiece = new ImageView(image);
            formatPieceImageView(p.isWhite,individualPiece);
            buttons[i].setGraphic(individualPiece);
            buttons[i].setDisable(false);
        }
    }
    private void formatPieceImageView(boolean isWhite,ImageView pieceImageView) {
        pieceImageView.setPreserveRatio(true);
        pieceImageView.setMouseTransparent(true);
        pieceImageView.fitWidthProperty().bind(grid.heightProperty().divide(9));
        if(isWhite)
            pieceImageView.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
        else
            pieceImageView.setEffect(glowEffect(Color.RED,Color.GOLD));
    }
    public void prefSizePropertyBind (DoubleBinding binding){
        minHeightProperty().bind(binding.divide(1.1));//this makes the pane smaller for some reason
        minWidthProperty().bind(binding.divide(1.1));//this makes the pane smaller for some reason

        maxHeightProperty().bind(binding.divide(1.1));//this makes dragging outside of board possible
        maxWidthProperty().bind(binding.divide(1.1));//this makes dragging outside of board possible

        grid.prefHeightProperty().bind(heightProperty().multiply(0.9075));
        grid.prefWidthProperty().bind(grid.prefHeightProperty());
        grid.minHeightProperty().bind(heightProperty().multiply(0.9075));
        grid.minWidthProperty().bind(grid.prefHeightProperty());
    }
}
