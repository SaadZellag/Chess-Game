package GUI.GameplayPanes;

import GUI.CustomButton;
import GUI.GameMode;
import engine.Engine;
import engine.MoveResult;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javafx.scene.paint.Color;

import static GUI.GUI.*;
import static GUI.GameMode.*;

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
    private boolean whiteIsBottom =true;

    private ImageView cloneView;

    public ObservableList<Move> moveHistoryList = FXCollections.observableArrayList();
    public LinkedList<Board> boardHistory= new LinkedList<>();

    private final Background dangerBackGround = getBackgroundImage("Board-modified.jpg",this,true);

    private final String SELECTED_COLOR = "rgba(255, 29, 255, 0.5)";
    private final String UNSELECTED_COLOR = "transparent";
//    final String UNSELECTED_COLOR="rgba(0, 0, 255, 0.5)";

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

    private final Runnable runOnGameOver;

    private final GameMode gameMode;

    long whiteRemainingTime = TimeUnit.MINUTES.toMillis(10);
    long blackRemainingTime= TimeUnit.MINUTES.toMillis(10);

    public ChessBoardPane(ReadOnlyDoubleProperty binding, Runnable runOnGameOver, GameMode gameMode){
        this.gameMode = gameMode;
        this.runOnGameOver=runOnGameOver;

        boardHistory.add(new Board(BitBoard.STARTING_POSITION_FEN));
        internalBoard=boardHistory.get(playedMovesCounter);
        draggingSurface.setMouseTransparent(true);
        try {
            grid= FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUI/Panes/BaseChessBoardPane.fxml")));
            getChildren().addAll(grid,draggingSurface);
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


            for (int i=0;i<promotionButtons.length;i++){
                promotionButtons[i]= new CustomButton(grid.heightProperty().divide(8));
                promotionMenu.getChildren().add(promotionButtons[i]);
            }
            promotionMenu.setStyle("-fx-background-color:rgba(0, 0, 255, 0.5)");

            startEngine();


//            addEventHandler(KeyEvent.KEY_PRESSED,e->{//todo this is for testing, remove later
//                int random = (int)(Math.random()*possibleMoves.size()-1);
//                Move move=possibleMoves.get(random);
//                animateMovePiece(move);
//            });
            Timer clockTimer= new Timer();
            clockTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(internalBoard.isWhiteTurn()==whiteIsBottom){
                        whiteRemainingTime-=1000;
                    }else{
                        blackRemainingTime-=1000;
                    }
                    if(blackRemainingTime==0||whiteRemainingTime==0){
                        cancel();
                        Platform.runLater(()->endGame(true));
                    }
                }
            },0, 1000L);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setDifficulty(Double difficulty){
        Engine.setDifficulty(difficulty);
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
            isInCheck();

        }
    }
    private void formatPieceImageView(boolean isWhite,ImageView pieceImageView) {
        pieceImageView.setPreserveRatio(true);
        pieceImageView.setMouseTransparent(true);
        pieceImageView.fitWidthProperty().bind(grid.heightProperty().divide(9));
        if(!whiteIsBottom)
            pieceImageView.setRotate(180);
        if(isWhite)
            pieceImageView.setEffect(glowEffect(Color.CYAN,Color.MAGENTA));
        else
            pieceImageView.setEffect(glowEffect(Color.RED,Color.GOLD));
        isInCheck();
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
            return;
        }
        clearSelectedTiles();

    }
    public void tileDragged(int draggedTileIndex, MouseEvent e){
        //Allows moving piece to tile even if you drag it slightly
        if(selectedPieceIndex!=draggedTileIndex&&buttons[draggedTileIndex].isSelected()&&!isDragging){
            animateMovePiece(selectedPieceIndex,draggedTileIndex);
            return;
        }

        //start dragging piece
        if (!isDragging&&buttons[draggedTileIndex].getGraphic()!=null){
            selectedPieceIndex =draggedTileIndex;
            displayPossibleMoves(draggedTileIndex);
            createDraggablePiece(draggedTileIndex);
            grid.setCursor(Cursor.CLOSED_HAND);
        }


        //do nothing if you drag an empty tile
        if (buttons[draggedTileIndex].getGraphic()==null){
            return;
        }

        double offset=cloneView.getFitWidth()/2*(whiteIsBottom ?-1:1);
        Point2D p=sceneToLocal(e.getSceneX()+offset,e.getSceneY()+offset);
        cloneView.relocate(p.getX(),p.getY());
        cloneView.setVisible(true);
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

        if(selectedPieceIndex ==newIndex&&isDragging){ //return piece to original position if there was no move done
            buttons[index].getGraphic().setVisible(true);
            buttons[index].setSelected(true);
            draggingSurface.getChildren().removeAll(cloneView);
            displayPossibleMoves(index);
            isDragging=false;
            return;
        }
        if(isDragging){//move piece to new location when dragging
            isDragging=false;
            draggingSurface.getChildren().removeAll(cloneView);
            movePiece(index,newIndex);
        }
    }
    public void displayPossibleMoves (int index){
        clearSelectedTiles();
        buttons[index].setSelected(true);
        String CURRENT_TILE_COLOR = "rgba(0, 255, 0, 0.5)";
        buttons[index].setStyle("-fx-background-color:"+ CURRENT_TILE_COLOR);

        //Cannot control opponent pieces unless it's local multiplayer
        if((gameMode.equals(SOLO)||gameMode.equals(ONLINE)) &&internalBoard.isWhiteTurn()!= whiteIsBottom)
            return;

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
        formatPieceImageView(internalBoard.getPieces()[index].isWhite,cloneView);
        cloneView.setVisible(false);
        draggingSurface.getChildren().add(cloneView);
        isDragging=!isDragging;
    }
    private void movePiece(int index, int newIndex) {
        loop: for (Move mv : possibleMoves) {
            if (!(mv.initialLocation == index && mv.finalLocation == newIndex)) {
                continue;
            }

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
        playedMovesCounter++;

        draggingSurface.getChildren().removeAll(cloneView);
        buttons[mv.initialLocation].setSelected(false);

        //Remove moves from history if you had undone
        while (boardHistory.size()>playedMovesCounter){
            boardHistory.remove(playedMovesCounter).toFEN();
            moveHistoryList.remove(playedMovesCounter-1);
        }

        //Store previous board state so that you can undo
        boardHistory.add(internalBoard.clone());
        internalBoard=boardHistory.get(playedMovesCounter);

        moveHistoryList.add(mv);
        clearSelectedTiles();
        isDragging=false;
        internalBoard.playMove(mv);

        placePieces();
        possibleMoves=internalBoard.generatePossibleMoves();
        if(possibleMoves.size()==0) {
            endGame(MoveGen.isInCheck(BitBoard.fromFEN(internalBoard.toFEN())));
            return;
        }

        startEngine();
    }

    private void startEngine() {
        if(gameMode.equals(SOLO)){
            Thread t= new Thread(()->{
                MoveResult bestMove=Engine.getBestMove(internalBoard,whiteIsBottom?blackRemainingTime : whiteRemainingTime);//todo
//                MoveResult bestMove=Engine.getBestMove(internalBoard,100);//todo if it has a lot of time it plays moves for the opponent
                ConfidenceBar.percentage=bestMove.confidence;

                if((internalBoard.isWhiteTurn()!= whiteIsBottom)&&!grid.isMouseTransparent())
                    Platform.runLater(()->animateMovePiece(bestMove.move));
            });
            t.start();
        }
    }

    private void isInCheck() {
        Piece[]pieces =internalBoard.getPieces();

        if(MoveGen.isInCheck(BitBoard.fromFEN(internalBoard.toFEN()))) {//Highlight king that is in check
            for(int i=0;i<pieces.length;i++){
                if(buttons[i].getGraphic()==null||pieces[i]==null)
                    continue;
                if(pieces[i].type==PieceType.KING&&pieces[i].isWhite==internalBoard.isWhiteTurn()){
                    buttons[i].getGraphic().setEffect(glowEffect(Color.RED,Color.color(0,0,0,0.5)));
                    break;
                }
            }
        }
    }
    private void animateMovePiece(int selectedPieceIndex, int newIndex) {

        createDraggablePiece(selectedPieceIndex);
        grid.setMouseTransparent(true);

        moveAnimationThread.schedule(new TimerTask() {
            int i=0;
            final double OFFSET =cloneView.getFitWidth()/2;

            final double MULTIPLIER =8;
            final double  initialX=buttons[selectedPieceIndex].getLayoutX()+ OFFSET;
            final double initialY=buttons[selectedPieceIndex].getLayoutY()+ OFFSET;
            final double finalX=buttons[newIndex].getLayoutX()+ OFFSET;
            final double finalY=buttons[newIndex].getLayoutY()+ OFFSET;

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
                Platform.runLater(()-> {
                    cloneView.relocate(initialX + i * slopeX, initialY + i * slopeY);
                    cloneView.setVisible(true);
                });
            }
        },0,1000/REFRESH_RATE);
    }
    private synchronized void animateMovePiece(Move mv) {//this method is to animate moves not done by the user

        createDraggablePiece(mv.initialLocation);
        buttons[mv.initialLocation].setSelected(false);
        grid.setMouseTransparent(true);

        moveAnimationThread.schedule(new TimerTask() {
            int i=0;
            final double OFFSET =cloneView.getFitWidth()/2;

            final double MULTIPLIER =8;
            final double  initialX=buttons[mv.initialLocation].getLayoutX()+ OFFSET;
            final double initialY=buttons[mv.initialLocation].getLayoutY()+ OFFSET;
            final double finalX=buttons[mv.finalLocation].getLayoutX()+ OFFSET;
            final double finalY=buttons[mv.finalLocation].getLayoutY()+ OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        finalizeMovePlay(mv);
                        grid.setMouseTransparent(false);
                    });
                }
                Platform.runLater(()-> {
                    cloneView.relocate(initialX + i * slopeX, initialY + i * slopeY);
                    cloneView.setVisible(true);
                });
            }
        },0,1000/REFRESH_RATE);
    }
    public void endGame(boolean isCheckmate) {
        setBackground(dangerBackGround);

        if(isCheckmate){
            Piece[] pieces = internalBoard.getPieces();
            for (int i = 0; i < pieces.length; i++) {
                if (pieces[i] == null)
                    continue;
                if (pieces[i].type == PieceType.KING && pieces[i].isWhite == internalBoard.isWhiteTurn()) {
                    buttons[i].setRotate(buttons[i].getRotate() + 90);
                    break;
                }
            }
        }

        runOnGameOver.run();
    }
    private void handlePromotion(Move mv) {

        if((gameMode.equals(SOLO)||gameMode.equals(ONLINE)) &&internalBoard.isWhiteTurn()!= whiteIsBottom) {//do not show your opponent's promotion menu
            piecePromoted(mv,mv.promotionPiece);
            return;
        }
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
        formatPieceImageView(internalBoard.isWhiteTurn(),pawn);
        buttons[mv.finalLocation].setGraphic(pawn);

        for (CustomButton promotionButton:promotionButtons)
            formatPieceImageView(internalBoard.isWhiteTurn(),(ImageView) promotionButton.getGraphic());


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
            final double OFFSET =cloneView.getFitWidth()/2;

            final double MULTIPLIER =6;
            final double  initialX=targetTower.getLayoutX()+OFFSET;
            final double initialY=targetTower.getLayoutY()+OFFSET;
            final double finalX=targetSquare.getLayoutX()+OFFSET;
            final double finalY=targetSquare.getLayoutY()+OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        finalizeMovePlay(mv);
                        grid.setMouseTransparent(false);
                        draggingSurface.getChildren().removeAll(cloneView);

                    });
                }
                Platform.runLater(()-> {
                    cloneView.relocate(initialX + i * slopeX, initialY + i * slopeY);
                    cloneView.setVisible(true);
                });
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
            final double OFFSET =cloneView.getFitWidth()/2;

            final double MULTIPLIER =6;
            final double  initialX=targetTower.getLayoutX()+OFFSET;
            final double initialY=targetTower.getLayoutY()+OFFSET;
            final double finalX=targetSquare.getLayoutX()+OFFSET;
            final double finalY=targetSquare.getLayoutY()+OFFSET;

            final double slopeX=(finalX-initialX)/REFRESH_RATE* MULTIPLIER;
            final double slopeY=(finalY-initialY)/REFRESH_RATE* MULTIPLIER;
            @Override
            public void run() {
                if (++i==REFRESH_RATE/ MULTIPLIER){
                    cancel();
                    Platform.runLater(()->{
                        finalizeMovePlay(mv);
                        grid.setMouseTransparent(false);
                        draggingSurface.getChildren().removeAll(cloneView);

                    });
                }
                Platform.runLater(()-> {
                    cloneView.relocate(initialX + i * slopeX, initialY + i * slopeY);
                    cloneView.setVisible(true);
                });
            }
        },0,1000/REFRESH_RATE);
    }
    public void rotatePieces() {
        for (ToggleButton button:buttons){
            if(button.getGraphic()!=null)
                button.getGraphic().setRotate(180);
        }
        whiteIsBottom =false;
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
