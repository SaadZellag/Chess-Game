package GUI.GameScene;

import engine.internal.BitBoard;
import game.Board;
import game.ChessUtils;
import game.Move;
import game.Piece;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.LinkedList;
import java.util.List;

import static GUI.GUI.getBackgroundImage;
import static GUI.GUI.getImage;

public class ChessBoardPane extends Pane{

    Board internalBoard;
    int playedMovesCounter=0;
    int initialPositionIndex;

    List<Move> possibleMoves;

    GridPane grid = new GridPane();
    ToggleButton[] buttons = new ToggleButton[64];
    boolean isDragging=false;
    int X_DRAGGING_OFFSET=45;
    int Y_DRAGGING_OFFSET=70;
    ImageView cloneView;

    LinkedList<Move> moveHistoryList = new LinkedList<>();
    LinkedList<Board>boardHistory= new LinkedList<>();

    Background normalBackGround = getBackgroundImage("Board.png",this,true);

    Background dangerBackGround = getBackgroundImage("Board-modified.jpg",this,true);

    final String TOGGLED_COLOR = "rgba(255, 0, 0, 0.49)";//red
    final String UNTOGGLED_COLOR = "transparent"/*"rgba(0, 0, 255, 0.49)"*/;//blue

    final Image W_PAWN=new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG");
    final Image W_ROOK= getImage("Board.png");
    final Image W_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image W_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image W_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image W_KING= getImage("Board-modified.jpg");

    final Image B_PAWN=new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG");
    final Image B_ROOK= getImage("Board.png");
    final Image B_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image B_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image B_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image B_KING= getImage("Board-modified.jpg");


    public ChessBoardPane(ReadOnlyDoubleProperty binding){
        boardHistory.add(new Board(BitBoard.STARTING_POSITION_FEN));
        internalBoard=boardHistory.get(playedMovesCounter);

        getChildren().add(grid);
        grid.setAlignment(Pos.CENTER);
        grid.setBackground(normalBackGround);
//        board.setStyle("-fx-background-color:green");

        //Create chess board
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                int index= i+8*j;
                final double RATIO=8.98;

                //ButtonTile properties
                buttons[index]=new ToggleButton();
                buttons[index].prefWidthProperty().bind(grid.widthProperty().divide(RATIO));
                buttons[index].prefHeightProperty().bind(grid.widthProperty().divide(RATIO));
//                buttonTiles[index].minHeightProperty().bind(board.widthProperty().divide(RATIO));
//                buttonTiles[index].minWidthProperty().bind(board.widthProperty().divide(RATIO));
                buttons[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                buttons[index].setPadding(Insets.EMPTY);
                buttons[index].setDisable(true);

                grid.add(buttons[index],i,j);
                buttons[index].setOnMouseDragged(e->tileDragged(index,e));
                buttons[index].setOnMouseReleased(e->tileReleased(index,e));
                buttons[index].setOnAction(e-> tileClicked(index));
//                buttons[index].setOnDragEntered(e-> System.out.println(index));
                buttons[index].selectedProperty().addListener(e->{
                    if(buttons[index].isSelected())
                        buttons[index].setStyle("-fx-background-color:"+TOGGLED_COLOR);
                    else
                        buttons[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                });

            }
        }
        placePieces();
        possibleMoves = internalBoard.generatePossibleMoves();
        prefSizePropertyBind(binding);
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
            individualPiece.setPreserveRatio(true);
            individualPiece.setMouseTransparent(true);
            individualPiece.fitHeightProperty().bind(grid.widthProperty().divide(1.1*8.99));

            buttons[i].setGraphic(individualPiece);
            buttons[i].setDisable(false);
        }
    }

    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        grid.prefHeightProperty().bind(binding.divide(1.1));
        grid.prefWidthProperty().bind(grid.prefHeightProperty());
        grid.minHeightProperty().bind(binding.divide(1.1));
        grid.minWidthProperty().bind(grid.prefHeightProperty());
        minHeightProperty().bind(binding.divide(1.1));
        minWidthProperty().bind(binding.divide(1.1));
        maxHeightProperty().bind(binding.divide(1.1));
        maxWidthProperty().bind(binding.divide(1.1));
    }

    public void tileClicked(int index) {
        if(buttons[index].isSelected()){
            if (buttons[index].getGraphic() != null) {
                initialPositionIndex = index;
                displayPossibleMoves(index);
            } else
//                movePiece(initialPositionIndex,index); TODO move pieces with clicking
                System.out.println("u dumb");
        }
        else
            clearSelectedTiles();
//        System.out.println(initialPositionIndex);

    }
    public void tileDragged(int index, MouseEvent e){//created copy of piece image that can be dragged around the screen
        if (buttons[index].getGraphic()!=null){
            if (!isDragging){
                displayPossibleMoves(index);
                createDraggablePiece(index);
            }
            Point2D p=localToParent(e.getSceneX()-X_DRAGGING_OFFSET,e.getSceneY()-Y_DRAGGING_OFFSET);

//            cloneView.relocate(e.getSceneX()-X_DRAGGING_OFFSET,e.getSceneY()-Y_DRAGGING_OFFSET);
            cloneView.relocate(p.getX(),p.getY());
        }
    }

    public void tileReleased(int index, MouseEvent e){
        Node releasedLocation=e.getPickResult().getIntersectedNode();
//        System.out.println(releasedLocation);
        isDragging=false;
        if(buttons[index].getGraphic()!=null){
            if (releasedLocation instanceof ToggleButton&&!releasedLocation.equals(buttons[index])&&((ToggleButton) releasedLocation).isSelected()){
                int newIndex=GridPane.getRowIndex(releasedLocation)*8+GridPane.getColumnIndex(releasedLocation);
                movePiece(index,newIndex);
            }
            else{
                buttons[index].getGraphic().setVisible(true);
                getChildren().remove(cloneView);
            }
        }
    }


    public void displayPossibleMoves (int index){
        clearSelectedTiles();
        buttons[index].setSelected(true);
        for(Move move:possibleMoves){
            if(index==move.initialLocation){
                buttons[move.finalLocation].setDisable(false);
                buttons[move.finalLocation].setSelected(true);
            }

        }
    }
    public void clearSelectedTiles(){
        for (ToggleButton button: buttons){
            button.setSelected(false);
            if (button.getGraphic()==null){
                button.setDisable(true);
            }

        }
    }
    private void createDraggablePiece(int index) {
        buttons[index].setSelected(true);
        buttons[index].getGraphic().setVisible(false);
        Image cloneImage= ((ImageView) buttons[index].getGraphic()).getImage();
        cloneView = new ImageView(cloneImage);
        cloneView.setPreserveRatio(true);
        cloneView.setMouseTransparent(true);
        cloneView.fitWidthProperty().bind(grid.widthProperty().divide(1.1*8.99));
        getChildren().add(cloneView);
        isDragging=!isDragging;
    }
    private void movePiece(int index, int newIndex) {
        playedMovesCounter++;
        System.out.println("after move "+playedMovesCounter);
        ImageView piece=(ImageView) buttons[index].getGraphic();
        buttons[index].setGraphic(null);
        piece.setVisible(true);
        buttons[newIndex].setGraphic(piece);
//        System.out.println(newIndex);
        getChildren().remove(cloneView);
        buttons[index].setSelected(false);

        while (boardHistory.size()>playedMovesCounter){
            System.out.println("Removed "+boardHistory.remove(playedMovesCounter).toFEN());
            System.out.println("Removed"+ChessUtils.moveToUCI(moveHistoryList.remove(playedMovesCounter-1)));
//            System.out.println(.toString());
        }

        boardHistory.add(internalBoard.clone());
        internalBoard=boardHistory.get(playedMovesCounter);
        System.out.println("Board before Move "+internalBoard.toFEN());

        for (Move mv : possibleMoves) {
            if (!(mv.initialLocation == index && mv.finalLocation == newIndex)) {
                continue;
            }
            moveHistoryList.add(mv);

            if(mv.moveInfo==null) {
                internalBoard.playMove(mv);
                continue;
            }

            switch (mv.moveInfo) {
                case EN_PASSANT -> handleEnPassant();
                case KING_CASTLE -> handleKingCastle(mv);
                case QUEEN_CASTLE -> handleQueenCaste(mv);
                case PROMOTION -> handlePromotion(mv);// TODO: Add the extra handling for promotion here
            }
            internalBoard.playMove(mv);

        }
        possibleMoves=internalBoard.generatePossibleMoves();
        if(possibleMoves.size()==0)
            endGame();
        System.out.println("Board after Move "+internalBoard.toFEN());
        clearSelectedTiles();
    }

    private void endGame() {//todo
        System.out.println("Checkmate");
        grid.setBackground(dangerBackGround);
    }

    private void handleEnPassant(){
        int target = internalBoard.getEnPassantTargetSquare();
        int aboveOrBellow = (internalBoard.isWhiteTurn() ? 8 : -8);
//                    System.out.println("target " + target);
//                    System.out.println("above " + aboveOrBellow);
        buttons[target + aboveOrBellow].setGraphic(null);

    }

    private void handlePromotion(Move mv) {//todo
    }

    private void handleQueenCaste(Move mv) {//todo
    }

    private void handleKingCastle(Move mv) {
        ImageView temp;
        if(internalBoard.isWhiteTurn()){
            temp = (ImageView) buttons[63].getGraphic();
            buttons[63].setGraphic(null);
        }else{
            temp = (ImageView) buttons[7].getGraphic();
            buttons[7].setGraphic(null);
        }
        buttons[mv.finalLocation-1].setGraphic(temp);
    }

    public void rotatePieces() {
        for (ToggleButton button:buttons){
            if(button.getGraphic()!=null)
                button.getGraphic().setRotate(180);
        }
        Y_DRAGGING_OFFSET=-20;
        X_DRAGGING_OFFSET=-10;
    }
    public void undo(){
        if(playedMovesCounter!=0){
            internalBoard=boardHistory.get(--playedMovesCounter);
            possibleMoves=internalBoard.generatePossibleMoves();
            placePieces();
            System.out.println("after undo "+playedMovesCounter);
            System.out.println("after undo "+internalBoard.toFEN());
        }

    }
}
