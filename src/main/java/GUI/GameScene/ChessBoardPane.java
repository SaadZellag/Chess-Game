package GUI.GameScene;

import engine.internal.BitBoard;
import game.Board;
import game.ChessUtils;
import game.Move;
import game.Piece;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.List;

import static GUI.GUI.getBackgroundImage;
import static GUI.GUI.getImage;

public class ChessBoardPane extends Pane{

    Board internalBoard;
    int playedMovesCounter=0;
    int selectedPieceIndex;

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

    final String CURRENT_TILE_COLOR = "rgba(0, 255, 0, 0.5)";//Green
    final String SELECTED_COLOR = "rgba(255, 0, 0, 0.5)";//red
    final String UNSELECTED_COLOR = "transparent"/*"rgba(0, 0, 255, 0.5)"*/;

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
                buttons[index].setStyle("-fx-background-color:"+ UNSELECTED_COLOR);
                buttons[index].setPadding(Insets.EMPTY);

                grid.add(buttons[index],i,j);
                buttons[index].setOnMouseDragged(e->tileDragged(index,e));
                buttons[index].setOnMouseReleased(e->tileReleased(index,e));
                buttons[index].setOnAction(e-> tileClicked(index));

                buttons[index].selectedProperty().addListener(e->{
                    if(buttons[index].isSelected())
                        buttons[index].setStyle("-fx-background-color:"+ SELECTED_COLOR);
                    else
                        buttons[index].setStyle("-fx-background-color:"+ UNSELECTED_COLOR);
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
            selectedPieceIndex = index;
            displayPossibleMoves(index);
            return;
        }
        if(selectedPieceIndex !=index){
            animateMovePiece(selectedPieceIndex,index);
            selectedPieceIndex=index;
            return;
        }
        clearSelectedTiles();

    }



    public void tileDragged(int draggedTileIndex, MouseEvent e){
        //start dragging piece
        if (!isDragging&&buttons[draggedTileIndex].getGraphic()!=null){
            selectedPieceIndex =draggedTileIndex;
            displayPossibleMoves(draggedTileIndex);
            createDraggablePiece(draggedTileIndex);
        }

        //Allows moving piece to tile even if you drag it slightly
        if(selectedPieceIndex!=draggedTileIndex&&buttons[draggedTileIndex].isSelected()){
            animateMovePiece(selectedPieceIndex,draggedTileIndex);
            return;
        }
        //do nothing if you drag an empty tile
        if (buttons[draggedTileIndex].getGraphic()==null){
            return;
        }


        Point2D p=localToParent(e.getSceneX()-X_DRAGGING_OFFSET,e.getSceneY()-Y_DRAGGING_OFFSET);
        cloneView.relocate(p.getX(),p.getY());
    }

    public void tileReleased(int index, MouseEvent e){

        if(buttons[index].getGraphic()==null){//do nothing if you were dragging an empty tile
            isDragging=false;
            return;
        }

        Node releasedLocation=e.getPickResult().getIntersectedNode();
        int newIndex=selectedPieceIndex;

        if (releasedLocation instanceof ToggleButton&&((ToggleButton) releasedLocation).isSelected())
            newIndex=GridPane.getRowIndex(releasedLocation)*8+GridPane.getColumnIndex(releasedLocation);

        if(selectedPieceIndex ==newIndex&&!e.isDragDetect()){//Releasing dragged piece on initial location keeps it selected
            buttons[index].getGraphic().setVisible(true);
            buttons[index].setSelected(true);
            getChildren().remove(cloneView);
            displayPossibleMoves(index);
            isDragging=false;
            return;
        }
        if(isDragging&&selectedPieceIndex !=newIndex){//move piece to new location
            isDragging=false;
            movePiece(index,newIndex);
            getChildren().remove(cloneView);
            return;
        }

        //return piece to original position
        isDragging=false;
        buttons[index].getGraphic().setVisible(true);
        getChildren().remove(cloneView);

    }


    public void displayPossibleMoves (int index){
        clearSelectedTiles();
        buttons[index].setSelected(true);
        buttons[index].setStyle("-fx-background-color:"+CURRENT_TILE_COLOR);
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
        getChildren().remove(cloneView);
        buttons[index].setSelected(false);

        while (boardHistory.size()>playedMovesCounter){
            System.out.println("Removed "+boardHistory.remove(playedMovesCounter).toFEN());
            System.out.println("Removed "+ChessUtils.moveToUCI(moveHistoryList.remove(playedMovesCounter-1)));
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
                break;
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
    private void animateMovePiece(int selectedPieceIndex, int newIndex) {
        TranslateTransition transition= new TranslateTransition();
        createDraggablePiece(selectedPieceIndex);
        double initialX=buttons[selectedPieceIndex].getLayoutX();
        double initialY=buttons[selectedPieceIndex].getLayoutY();
        cloneView.relocate(initialX,initialY);
        transition.setNode(cloneView);
        Point2D p=localToScene(buttons[newIndex].getLayoutX(),buttons[newIndex].getLayoutY());
        transition.setToX(p.getX());
        transition.setToY(p.getY());
        transition.setDuration(Duration.seconds(0.2));
        transition.play();
        transition.setOnFinished(e->{System.out.println("X and Y "+cloneView.getLayoutX()+" "+cloneView.getLayoutY());
            movePiece(selectedPieceIndex,newIndex);

        });
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
            clearSelectedTiles();
            System.out.println("after undo "+playedMovesCounter);
            System.out.println("after undo "+internalBoard.toFEN());
        }

    }
}
