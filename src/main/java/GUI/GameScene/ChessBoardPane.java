package GUI.GameScene;

import game.Board;
import game.Move;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.LinkedList;
import java.util.List;

public class ChessBoardPane extends Pane{
    Board internalBoard;
    int playedMovesCounter=0;
    int initialPositionIndex;
    List<Move> possibleMoves;

    boolean whiteIsBottom;

    GridPane grid = new GridPane();
    ToggleButton[] buttons = new ToggleButton[64];
    boolean isDragging=false;
    int X_DRAGGING_OFFSET=50;
    int Y_DRAGGING_OFFSET=50;
    ImageView cloneView;

    LinkedList<Move> moveHistory = new LinkedList<Move>();

    BackgroundImage normalBackImage = new BackgroundImage(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(grid.getWidth(), grid.getHeight(), true, true, true, false));
    Background normalBackGround = new Background(normalBackImage);

    BackgroundImage dangerBackImage = new BackgroundImage(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board-modified.jpg")), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(grid.getWidth(), grid.getHeight(), true, true, true, false));
    Background dangerBackGround = new Background(dangerBackImage);

    final String TOGGLED_COLOR = "rgba(255, 0, 0, 0.49)";//red
    final String UNTOGGLED_COLOR = "transparent"/*"rgba(0, 0, 255, 0.49)"*/;//blue

    final ImageView[][] PIECES= new ImageView[][]{
            new ImageView[8],//W_PAWN
            new ImageView[2],//W_ROOK
            new ImageView[2],//W_KNIGHT
            new ImageView[2],//W_BISHOP
            new ImageView[1],//W_QUEEN
            new ImageView[1],//W_KING

            new ImageView[8],//B_PAWN
            new ImageView[2],//B_ROOK
            new ImageView[2],//B_KNIGHT
            new ImageView[2],//B_BISHOP
            new ImageView[1],//B_QUEEN
            new ImageView[1],//B_KING
    };
    final Image W_PAWN=new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG");
    final Image W_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png"));
    final Image W_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image W_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image W_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image W_KING= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board-modified.jpg"));

    final Image B_PAWN=new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG");
    final Image B_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.png"));
    final Image B_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image B_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image B_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image B_KING= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board-modified.jpg"));


    public ChessBoardPane(ReadOnlyDoubleProperty binding,boolean whiteIsBottom){
        this.whiteIsBottom=false;
        String fen= "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        internalBoard = new Board(fen);
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
                buttons[index].setOnDragEntered(e-> System.out.println(index));
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
        int i=0;
        for (ImageView[] pieceType:PIECES) {
            int j=0;
            for (ImageView individualPiece:pieceType) {
                switch (i) {
                    case 0->{individualPiece= new ImageView(W_PAWN);
                        buttons[(whiteIsBottom? 48:8)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 48:8)+j].setDisable(false);
                        j++;
                    }
                    case 1->{individualPiece= new ImageView(W_ROOK);
                        buttons[(whiteIsBottom? 55+i:0)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 55+i:0)+j].setDisable(false);
                        j+=7;
                    }
                    case 2->{individualPiece= new ImageView(W_KNIGHT);
                        buttons[(whiteIsBottom? 55+i:-1+i)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 55+i:-1+i)+j].setDisable(false);
                        j+=5;
                    }
                    case 3->{individualPiece= new ImageView(W_BISHOP);
                        buttons[(whiteIsBottom? 55+i:-1+i)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 55+i:-1+i)+j].setDisable(false);
                        j+=3;
                    }
                    case 4->{individualPiece= new ImageView(W_QUEEN);
                        buttons[(whiteIsBottom? 55+i:4)].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 55+i:4)].setDisable(false);
                    }
                    case 5->{individualPiece= new ImageView(W_KING);
                        buttons[(whiteIsBottom? 55+i:3)].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 55+i:3)].setDisable(false);
                    }
                    case 6->{individualPiece= new ImageView(B_PAWN);
                        buttons[(whiteIsBottom? 8:48)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 8:48)+j].setDisable(false);
                        j++;
                    }
                    case 7->{individualPiece= new ImageView(B_ROOK);
                        buttons[(whiteIsBottom? 0:55+i-6)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? 0:55+i-6)+j].setDisable(false);
                        j+=7;
                    }
                    case 8->{individualPiece= new ImageView(B_KNIGHT);
                        buttons[(whiteIsBottom? i-7:55+i-6)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? i-7:55+i-6)+j].setDisable(false);
                        j+=5;
                    }
                    case 9->{individualPiece= new ImageView(B_BISHOP);
                        buttons[(whiteIsBottom? i-7:55+i-6)+j].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? i-7:55+i-6)+j].setDisable(false);
                        j+=3;
                    }
                    case 10->{individualPiece= new ImageView(B_QUEEN);
                        buttons[(whiteIsBottom? i-7:60)].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? i-7:60)].setDisable(false);
                    }
                    case 11->{individualPiece= new ImageView(B_KING);
                        buttons[(whiteIsBottom? i-7:59)].setGraphic(individualPiece);
                        buttons[(whiteIsBottom? i-7:59)].setDisable(false);
                    }
                }
                individualPiece.setPreserveRatio(true);
                individualPiece.setMouseTransparent(true);
                individualPiece.fitHeightProperty().bind(grid.widthProperty().divide(1.1*8.99));
            }
            i++;

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
        System.out.println(initialPositionIndex);

    }
    public void tileDragged(int index, MouseEvent e){//created copy of piece image that can be dragged around the screen
        if (buttons[index].getGraphic()!=null){
            if (!isDragging){
                displayPossibleMoves(index);
                createDraggablePiece(index);
            }
            cloneView.relocate(e.getSceneX()-X_DRAGGING_OFFSET,e.getSceneY()-Y_DRAGGING_OFFSET);
        }
    }

    public void tileReleased(int index, MouseEvent e){
        Node releasedLocation=e.getPickResult().getIntersectedNode();
        System.out.println(releasedLocation);
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
        index=whiteIsBottom?index:rotateBoardIndex(index);
        for(Move move:possibleMoves){
            if(index==move.initialLocation){
                buttons[whiteIsBottom?move.finalLocation:rotateBoardIndex(move.finalLocation)].setDisable(false);
                buttons[whiteIsBottom?move.finalLocation:rotateBoardIndex(move.finalLocation)].setSelected(true);
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
        ImageView piece=(ImageView) buttons[index].getGraphic();
        buttons[index].setGraphic(null);
        piece.setVisible(true);
        buttons[newIndex].setGraphic(piece);
        System.out.println(newIndex);
        getChildren().remove(cloneView);
        buttons[index].setSelected(false);

        if(!whiteIsBottom){
            index=rotateBoardIndex(index);
            newIndex=rotateBoardIndex(newIndex);
        }
        for (Move mv : possibleMoves) {
            if (mv.initialLocation == index && mv.finalLocation == newIndex) {
                moveHistory.add(mv);
                if(mv.moveInfo!=null){
                    switch (mv.moveInfo) {
                        case EN_PASSANT -> {
                            int target = whiteIsBottom?internalBoard.getEnPassantTargetSquare():rotateBoardIndex(internalBoard.getEnPassantTargetSquare());
                            int aboveOrBellow = (whiteIsBottom ? 1 : -1) * (internalBoard.isWhiteTurn() ? 8 : -8);
                            buttons[target + aboveOrBellow].setGraphic(null);
                        }
                        case KING_CASTLE -> handleKingCastle(mv);
                        case QUEEN_CASTLE -> handleQueenCaste(mv);
                        case PROMOTION -> handlePromotion(mv);
                    }
                }
                internalBoard.playMove(moveHistory.get(playedMovesCounter++));

                // TODO: Add the extra handling for promotion here

            }
        }
        possibleMoves=internalBoard.generatePossibleMoves();
        if(possibleMoves.size()==0)
            endGame();
        System.out.println(internalBoard.toFEN());
        clearSelectedTiles();
    }

    private void endGame() {//todo
        System.out.println("Checkmate");
        grid.setBackground(dangerBackGround);
    }

    private void handlePromotion(Move mv) {//todo
    }

    private void handleQueenCaste(Move mv) {//todo
    }

    private void handleKingCastle(Move mv) {
        if(whiteIsBottom){
            ImageView temp;
            if(internalBoard.isWhiteTurn()){
                temp = (ImageView) buttons[63].getGraphic();
                buttons[63].setGraphic(null);
            }else{
                temp = (ImageView) buttons[7].getGraphic();
                buttons[7].setGraphic(null);
            }
            buttons[mv.finalLocation-1].setGraphic(temp);
        }else{
            ImageView temp;
            if(internalBoard.isWhiteTurn()){
                temp = (ImageView) buttons[0].getGraphic();
                buttons[0].setGraphic(null);
            }else{
                temp = (ImageView) buttons[56].getGraphic();
                buttons[56].setGraphic(null);
            }
            buttons[rotateBoardIndex(mv.finalLocation)+1].setGraphic(temp);
        }
    }

    private int rotateBoardIndex(int index){
        int newY=7-index/8;
        int newX=7-index%8;
        return newX+newY*8;
    }
}
