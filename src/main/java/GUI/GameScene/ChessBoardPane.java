package GUI.GameScene;

import engine.internal.BitBoard;
import engine.internal.MoveGen;
import game.Board;
import game.Move;
import game.Piece;
import game.PieceType;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.*;

import static GUI.GUI.*;

public class ChessBoardPane extends StackPane{

    private final VBox promotionMenu= new VBox();
    private final CustomButton[] promotionButtons=new CustomButton[4];

    private final Timer moveAnimationThread = new Timer(true);

    public Board internalBoard;
    private int playedMovesCounter=0;
    private int selectedPieceIndex;

    private List<Move> possibleMoves;

    private GridPane grid;
    private final Pane draggingSurface=new Pane();

    private final ToggleButton[] buttons = new ToggleButton[64];
    private boolean isDragging=false;
    private boolean isRotated=false;

    public double X_DRAGGING_OFFSET=45;
    public double Y_DRAGGING_OFFSET=70;

    public double X_ANIMATION_OFFSET =0;
    public double Y_ANIMATION_OFFSET =0;
    private ImageView cloneView;

    public ObservableList<Move> moveHistoryList = FXCollections.observableArrayList();
    public LinkedList<Board> boardHistory= new LinkedList<>();

    private final Background normalBackGround = getBackgroundImage("Board.png",this,true);

    private final Background dangerBackGround = getBackgroundImage("Board-modified.jpg",this,true);

    private final String CURRENT_TILE_COLOR = "rgba(0, 255, 0, 0.5)";
    private final String SELECTED_COLOR = "rgba(255, 29, 255, 0.5)";
    private final String UNSELECTED_COLOR = "transparent";
//    final String UNSELECTED_COLOR="rgba(0, 0, 255, 0.5)";

    private final Image W_PAWN=new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-50_14db7352-cb40-484f-bcaf-ec13fdeba785_1200x1200.png?v=1535644667");
    private final Image W_ROOK= new Image("https://cdn.shopify.com/s/files/1/2209/1363/products/additional_megachess-22_fd93f1cb-c583-41a8-8ec8-d4fe10814a42_1024x.png?v=1535651942");
    private final Image W_KNIGHT= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/2017_Mega_Chess_Need_to_Clip-55_749bbdd5-8df4-41f1-b663-2a9f5b81fab7_1200x1200.png?v=1535652156");
    private final Image W_BISHOP= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-46_033c4a97-c8e1-4e86-bbc7-c9b0b306e5fd_1200x1200.png?v=1535645303");
    private final Image W_QUEEN= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-18_1200x1200.png?v=1535655093");
    private final Image W_KING= new Image("https://cdn.shopify.com/s/files/1/2209/1363/products/additional_megachess-44_33afb3f4-7bfe-4438-a46a-46b5d1b35078.png?v=1535649501");

    private final Image B_PAWN=new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-50_14db7352-cb40-484f-bcaf-ec13fdeba785_1200x1200.png?v=1535644667");
    private final Image B_ROOK= new Image("https://cdn.shopify.com/s/files/1/2209/1363/products/additional_megachess-22_fd93f1cb-c583-41a8-8ec8-d4fe10814a42_1024x.png?v=1535651942");
    private final Image B_KNIGHT= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/2017_Mega_Chess_Need_to_Clip-55_749bbdd5-8df4-41f1-b663-2a9f5b81fab7_1200x1200.png?v=1535652156");
    private final Image B_BISHOP= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-46_033c4a97-c8e1-4e86-bbc7-c9b0b306e5fd_1200x1200.png?v=1535645303");
    private final Image B_QUEEN= new Image("https://cdn.shopify.com/s/files/1/2209/1479/products/additional_megachess-18_1200x1200.png?v=1535655093");
    private final Image B_KING= new Image("https://cdn.shopify.com/s/files/1/2209/1363/products/additional_megachess-44_33afb3f4-7bfe-4438-a46a-46b5d1b35078.png?v=1535649501");

    private final Runnable runOnGameOver;

    public ChessBoardPane(ReadOnlyDoubleProperty binding,Runnable runOnGameOver){
        this.runOnGameOver=runOnGameOver;

        boardHistory.add(new Board(BitBoard.STARTING_POSITION_FEN));
        internalBoard=boardHistory.get(playedMovesCounter);
        draggingSurface.setMouseTransparent(true);
        try {
            grid= FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUI/Panes/BaseChessBoardPane.fxml")));
            getChildren().addAll(grid,draggingSurface);
            grid.setAlignment(Pos.CENTER);
            setBackground(normalBackGround);
//            setStyle("-fx-background-color:green");

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

            heightProperty().addListener(e->{//makes it so the animations/dragging stay aligned even when the window is resized
                X_ANIMATION_OFFSET=getHeight()/22;
                Y_ANIMATION_OFFSET=getHeight()/22;
//
                X_DRAGGING_OFFSET=getHeight()/12.2;
                Y_DRAGGING_OFFSET=getHeight()/8;
            });
            placePieces();
            possibleMoves = internalBoard.generatePossibleMoves();
            prefSizePropertyBind(binding);


            for (int i=0;i<promotionButtons.length;i++){
                promotionButtons[i]= new CustomButton(grid.heightProperty().divide(8),grid.heightProperty().divide(8));
                promotionMenu.getChildren().add(promotionButtons[i]);
            }
            promotionMenu.setStyle("-fx-background-color:rgba(0, 0, 255, 0.5)");


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
            formatPieceImageView(individualPiece);

            buttons[i].setGraphic(individualPiece);
            buttons[i].setDisable(false);

            if(!p.isWhite)//TODO gotta remove this in the final version
              buttons[i].getGraphic().setEffect(new ColorAdjust(0,0.2,-0.8,1));
        }
    }
    private void formatPieceImageView(ImageView pieceImageView) {
        pieceImageView.setPreserveRatio(true);
        pieceImageView.setMouseTransparent(true);
        pieceImageView.fitWidthProperty().bind(grid.heightProperty().divide(8));
        if(isRotated)
            pieceImageView.setRotate(180);
    }

    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        minHeightProperty().bind(binding.divide(1.1));//this makes the pane smaller for some reason
        minWidthProperty().bind(binding.divide(1.1));//this makes the pane smaller for some reason

        maxHeightProperty().bind(binding.divide(1.1));//this makes dragging outside of board possible
        maxWidthProperty().bind(binding.divide(1.1));//this makes dragging outside of board possible

        grid.prefHeightProperty().bind(heightProperty().multiply(0.9075));
        grid.prefWidthProperty().bind(grid.prefHeightProperty());
        grid.minHeightProperty().bind(heightProperty().multiply(0.9075));
        grid.minWidthProperty().bind(grid.prefHeightProperty());
    }

    public void tileClicked(int index) {
        if(buttons[index].isSelected()){
            selectedPieceIndex = index;
            displayPossibleMoves(index);
            return;
        }
        if(selectedPieceIndex !=index){
            animateMovePiece(selectedPieceIndex,index);
//            selectedPieceIndex=index;
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
            grid.setCursor(Cursor.NONE);
        }

        //Allows moving piece to tile even if you drag it slightly
        if(selectedPieceIndex!=draggedTileIndex&&buttons[draggedTileIndex].isSelected()&&!isDragging){
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
        grid.setCursor(Cursor.DEFAULT);
        if(buttons[index].getGraphic()==null||grid.isMouseTransparent()){//do nothing if you were dragging an empty tile or if animating
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
            draggingSurface.getChildren().remove(cloneView);
            displayPossibleMoves(index);
            isDragging=false;
            return;
        }
        if(isDragging&&selectedPieceIndex !=newIndex){//move piece to new location when dragging
            isDragging=false;
            draggingSurface.getChildren().remove(cloneView);
            movePiece(index,newIndex);

            return;
        }

        //return piece to original position if there was no move done
        isDragging=false;
        buttons[index].getGraphic().setVisible(true);
        draggingSurface.getChildren().remove(cloneView);

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
        formatPieceImageView(cloneView);
        draggingSurface.getChildren().add(cloneView);
        isDragging=!isDragging;
    }

    private void movePiece(int index, int newIndex) {
        playedMovesCounter++;

        draggingSurface.getChildren().remove(cloneView);
        buttons[index].setSelected(false);

        //Remove moves from history if you had undone
        while (boardHistory.size()>playedMovesCounter){
            boardHistory.remove(playedMovesCounter).toFEN();
            moveHistoryList.remove(playedMovesCounter-1);
        }

        //Store previous board state so that you can undo
        boardHistory.add(internalBoard.clone());
        internalBoard=boardHistory.get(playedMovesCounter);


        loop: for (Move mv : possibleMoves) {
            if (!(mv.initialLocation == index && mv.finalLocation == newIndex)) {
                continue;
            }
            moveHistoryList.add(mv);

            if(mv.moveInfo==null) {
                finalizeMovePlay(mv);
                break;
            }
            switch (mv.moveInfo) {
                case EN_PASSANT -> {finalizeMovePlay(mv); break loop;}
                case KING_CASTLE -> {handleKingCastle(mv); break loop;}
                case QUEEN_CASTLE -> {handleQueenCaste(mv);break loop;}
                case PROMOTION -> {handlePromotion(mv); break loop;}
            }
        }

    }

    private void finalizeMovePlay(Move mv) {//play move both internally and for user
        clearSelectedTiles();
        isDragging=false;
        internalBoard.playMove(mv);
        placePieces();
        possibleMoves=internalBoard.generatePossibleMoves();
        isInCheck();
        if(possibleMoves.size()==0)
            endGame();

    }

    private void isInCheck() {
        if(MoveGen.isInCheck(BitBoard.fromFEN(internalBoard.toFEN())))
            setBackground(dangerBackGround);
        else
            setBackground(normalBackGround);
    }

    private void animateMovePiece(int selectedPieceIndex, int newIndex) {

        createDraggablePiece(selectedPieceIndex);
        grid.setMouseTransparent(true);

        moveAnimationThread.schedule(new TimerTask() {
            int i=0;

            final double MULTIPLIER =8;
            final double  initialX=buttons[selectedPieceIndex].getLayoutX()+X_ANIMATION_OFFSET;
            final double initialY=buttons[selectedPieceIndex].getLayoutY()+Y_ANIMATION_OFFSET;
            final double finalX=buttons[newIndex].getLayoutX()+X_ANIMATION_OFFSET;
            final double finalY=buttons[newIndex].getLayoutY()+Y_ANIMATION_OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        movePiece(selectedPieceIndex,newIndex);
                        grid.setMouseTransparent(false);
                    });
                }
                Platform.runLater(()->cloneView.relocate(initialX+ i*slopeX,initialY+ i*slopeY));
            }
        },0,1000/REFRESH_RATE);
    }

    private void endGame() {//todo
        System.out.println("Checkmate");
        runOnGameOver.run();
    }

    private void handlePromotion(Move mv) {
        grid.setMouseTransparent(true);
        draggingSurface.setMouseTransparent(false);
        draggingSurface.getChildren().add(promotionMenu);

        ImageView rook= new ImageView(internalBoard.isWhiteTurn()?W_ROOK:B_ROOK);
        promotionButtons[0].setGraphic(rook);

        ImageView bishop= new ImageView(internalBoard.isWhiteTurn()?W_BISHOP:B_BISHOP);
        promotionButtons[1].setGraphic(bishop);

        ImageView queen= new ImageView(internalBoard.isWhiteTurn()?W_QUEEN:B_QUEEN);
        promotionButtons[2].setGraphic(queen);

        ImageView knight= new ImageView(internalBoard.isWhiteTurn()?W_KNIGHT:B_KNIGHT);
        promotionButtons[3].setGraphic(knight);

        ImageView pawn= new ImageView(internalBoard.isWhiteTurn()?W_PAWN:B_PAWN);
        formatPieceImageView(pawn);
        buttons[mv.finalLocation].setGraphic(pawn);

        if(isRotated)
            for (CustomButton promotionButton:promotionButtons)
                promotionButton.getGraphic().setRotate(180);


        promotionButtons[0].setOnAction(e->piecePromoted(mv,PieceType.ROOK));
        promotionButtons[1].setOnAction(e->piecePromoted(mv,PieceType.BISHOP));
        promotionButtons[2].setOnAction(e->piecePromoted(mv,PieceType.QUEEN));
        promotionButtons[3].setOnAction(e->piecePromoted(mv,PieceType.KNIGHT));


    }



    private void piecePromoted(Move mv,PieceType pieceType) {
        for (Move possibleMove:possibleMoves) {
            if(possibleMove.promotionPiece==pieceType&&possibleMove.finalLocation==mv.finalLocation){
                finalizeMovePlay(possibleMove);
                break;
            }
        }
        grid.setMouseTransparent(false);
        draggingSurface.getChildren().remove(promotionMenu);
        draggingSurface.setMouseTransparent(true);
    }

    private void handleQueenCaste(Move mv) {
        buttons[mv.finalLocation].setGraphic(cloneView);
        createDraggablePiece(internalBoard.isWhiteTurn()?56:0);
        grid.setMouseTransparent(true);

        moveAnimationThread.schedule(new TimerTask() {
            int i=0;
            final ToggleButton targetTower= internalBoard.isWhiteTurn()?buttons[56]:buttons[0];
            final ToggleButton targetSquare=buttons[mv.finalLocation+1];

            final double MULTIPLIER =6;
            final double  initialX=targetTower.getLayoutX()+X_ANIMATION_OFFSET;
            final double initialY=targetTower.getLayoutY()+Y_ANIMATION_OFFSET;
            final double finalX=targetSquare.getLayoutX()+X_ANIMATION_OFFSET;
            final double finalY=targetSquare.getLayoutY()+Y_ANIMATION_OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        finalizeMovePlay(mv);
                        grid.setMouseTransparent(false);
                        draggingSurface.getChildren().remove(cloneView);

                    });
                }
                Platform.runLater(()->cloneView.relocate(initialX+ i*slopeX,initialY+ i*slopeY));
            }
        },0,1000/REFRESH_RATE);
    }

    private void handleKingCastle(Move mv) {
        buttons[mv.finalLocation].setGraphic(cloneView);
        createDraggablePiece(internalBoard.isWhiteTurn()?63:7);
        grid.setMouseTransparent(true);

        moveAnimationThread.schedule(new TimerTask() {
            int i=0;
            final ToggleButton targetTower= internalBoard.isWhiteTurn()?buttons[63]:buttons[7];
            final ToggleButton targetSquare=buttons[mv.finalLocation-1];

            final double MULTIPLIER =6;
            final double  initialX=targetTower.getLayoutX()+X_ANIMATION_OFFSET;
            final double initialY=targetTower.getLayoutY()+Y_ANIMATION_OFFSET;
            final double finalX=targetSquare.getLayoutX()+X_ANIMATION_OFFSET;
            final double finalY=targetSquare.getLayoutY()+Y_ANIMATION_OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        finalizeMovePlay(mv);
                        grid.setMouseTransparent(false);
                        draggingSurface.getChildren().remove(cloneView);

                    });
                }
                Platform.runLater(()->cloneView.relocate(initialX+ i*slopeX,initialY+ i*slopeY));
            }
        },0,1000/REFRESH_RATE);
    }

    public void rotatePieces() {
        for (ToggleButton button:buttons){
            if(button.getGraphic()!=null)
                button.getGraphic().setRotate(180);
        }
        isRotated=true;
        Y_DRAGGING_OFFSET=-20;
        X_DRAGGING_OFFSET=-10;
    }
    public void undo(){
        if(playedMovesCounter!=0){
            if(promotionMenu.getParent()!=null)
                return;
            internalBoard=boardHistory.get(--playedMovesCounter);
            possibleMoves=internalBoard.generatePossibleMoves();
            placePieces();
            isInCheck();
            clearSelectedTiles();
        }

    }
    public void redo() {
        if(boardHistory.size()-1>playedMovesCounter){
            internalBoard=boardHistory.get(++playedMovesCounter);
            possibleMoves=internalBoard.generatePossibleMoves();
            placePieces();
            isInCheck();
            clearSelectedTiles();
        }
    }
}
