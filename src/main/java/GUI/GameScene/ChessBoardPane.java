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
    Board idkwhattocalllthis;
    int moveCounter=0;
    int initialPositionIndex;

    GridPane board= new GridPane();
    ToggleButton[] buttonTiles= new ToggleButton[64];
    boolean isDragging=false;
    int X_DRAGGING_OFFSET=50;
    int Y_DRAGGING_OFFSET=50;
    ImageView cloneView;

    LinkedList<Move> moves= new LinkedList<Move>();
    Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(board.getWidth(), board.getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);
    final String TOGGLED_COLOR = "rgba(255, 0, 0, 0.49)";//red
    final String UNTOGGLED_COLOR = "rgba(0, 0, 255, 0.49)";//blue

    final ImageView[] W_PAWN= new ImageView[16];
    final ImageView[] W_ROOK= new ImageView[2];
//    final Image W_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image W_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image W_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image W_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image W_KING= new Image("http://s.thestreet.com/files/tsc/v2008/photos/contrib/uploads/28205704-4d5f-11e8-a2db-e76c960a6c05.png");
    final ImageView[] B_PAWN= new ImageView[16];
    final Image B_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_KNIGHT= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_BISHOP= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_QUEEN= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_KING= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));


    public ChessBoardPane(ReadOnlyDoubleProperty binding,boolean whiteIsBottom){
        String fen= whiteIsBottom? "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1" : "PPPPPPPP/RNBQKBNR/8/8/8/8/rnbqkbnr/pppppppp w KQkq - 0 1";
        idkwhattocalllthis= new Board(fen);

        getChildren().add(board);
        board.setAlignment(Pos.CENTER);
        board.setBackground(backGround);
//        board.setStyle("-fx-background-color:green");

        //Create chess board
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                int index= i+8*j;
                final double RATIO=8.98;

                //ButtonTile properties
                buttonTiles[index]=new ToggleButton();
                buttonTiles[index].prefWidthProperty().bind(board.widthProperty().divide(RATIO));
                buttonTiles[index].prefHeightProperty().bind(board.widthProperty().divide(RATIO));
//                buttonTiles[index].minHeightProperty().bind(board.widthProperty().divide(RATIO));
//                buttonTiles[index].minWidthProperty().bind(board.widthProperty().divide(RATIO));
                buttonTiles[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                buttonTiles[index].setPadding(Insets.EMPTY);
                buttonTiles[index].setDisable(true);

                board.add(buttonTiles[index],i,j);
                buttonTiles[index].setOnMouseDragged(e->tileDragged(index,e));
                buttonTiles[index].setOnMouseReleased(e->tileReleased(index,e));
                buttonTiles[index].setOnAction(e-> tileClicked(index));
                buttonTiles[index].setOnDragEntered(e-> System.out.println(index));
                buttonTiles[index].selectedProperty().addListener(e->{
                    if(buttonTiles[index].isSelected())
                        buttonTiles[index].setStyle("-fx-background-color:"+TOGGLED_COLOR);
                    else
                        buttonTiles[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                });

            }

            W_PAWN[i]= new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG"));
            W_PAWN[i].setPreserveRatio(true);
            W_PAWN[i].setMouseTransparent(true);
            W_PAWN[i].fitHeightProperty().bind(board.widthProperty().divide(1.1*8.99));
            buttonTiles[48+i].setGraphic(W_PAWN[i]);
            buttonTiles[48+i].setDisable(false);

            W_PAWN[8+i]= new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG"));
            W_PAWN[8+i].setPreserveRatio(true);
            W_PAWN[8+i].setMouseTransparent(true);
            W_PAWN[8+i].fitHeightProperty().bind(board.widthProperty().divide(1.1*8.99));
            buttonTiles[56+i].setGraphic(W_PAWN[8+i]);
            buttonTiles[56+i].setDisable(false);

            B_PAWN[i]= new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg")));
            B_PAWN[i].setPreserveRatio(true);
            B_PAWN[i].setMouseTransparent(true);
            B_PAWN[i].fitHeightProperty().bind(board.widthProperty().divide(1.1*8.99));
            buttonTiles[8+i].setGraphic(B_PAWN[i]);
            buttonTiles[8+i].setDisable(false);

            B_PAWN[8+i]= new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg")));
            B_PAWN[8+i].setPreserveRatio(true);
            B_PAWN[8+i].setMouseTransparent(true);
            B_PAWN[8+i].fitHeightProperty().bind(board.widthProperty().divide(1.1*8.99));
            buttonTiles[0+i].setGraphic(B_PAWN[8+i]);
            buttonTiles[0+i].setDisable(false);
        }
        prefSizePropertyBind(binding);
    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        board.prefHeightProperty().bind(binding.divide(1.1));
        board.prefWidthProperty().bind(board.prefHeightProperty());
        board.minHeightProperty().bind(binding.divide(1.1));
        board.minWidthProperty().bind(board.prefHeightProperty());
        minHeightProperty().bind(binding.divide(1.1));
        minWidthProperty().bind(binding.divide(1.1));
        maxHeightProperty().bind(binding.divide(1.1));
        maxWidthProperty().bind(binding.divide(1.1));
    }

    public void tileClicked(int index) {
        if(buttonTiles[index].isSelected()){
            if (buttonTiles[index].getGraphic() != null) {
                initialPositionIndex = index;
                displayPossibleMoves(index);
            } else
                movePiece(initialPositionIndex,index);
        }
        else
            clearPossibleMoves();
        System.out.println(initialPositionIndex);

    }
    public void tileDragged(int index, MouseEvent e){//created copy of piece image that can be dragged around the screen
        if (buttonTiles[index].getGraphic()!=null){
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
        if(buttonTiles[index].getGraphic()!=null){
            if (releasedLocation instanceof ToggleButton&&!releasedLocation.equals(buttonTiles[index])&&((ToggleButton) releasedLocation).isSelected()){
                int newIndex=GridPane.getRowIndex(releasedLocation)*8+GridPane.getColumnIndex(releasedLocation);
                movePiece(index,newIndex);
            }
            else{
                buttonTiles[index].getGraphic().setVisible(true);
                getChildren().remove(cloneView);
            }
        }
    }


    public void displayPossibleMoves (int index){
        clearPossibleMoves();
        buttonTiles[index].setSelected(true);
        List<Move> possibleMoves=idkwhattocalllthis.generatePossibleMoves();
        for(Move move:possibleMoves){
            if(index==move.initialLocation){
                buttonTiles[move.finalLocation].setDisable(false);
                buttonTiles[move.finalLocation].setSelected(true);
            }

        }
    }
    public void clearPossibleMoves(){
        for (ToggleButton button:buttonTiles){
            button.setSelected(false);
            if (button.getGraphic()==null){//TODO or if it's opponent's turn
                button.setDisable(true);
            }

        }
    }
    private void createDraggablePiece(int index) {
        buttonTiles[index].setSelected(true);
        buttonTiles[index].getGraphic().setVisible(false);
        Image cloneImage= ((ImageView) buttonTiles[index].getGraphic()).getImage();
        cloneView = new ImageView(cloneImage);
        cloneView.setPreserveRatio(true);
        cloneView.setMouseTransparent(true);
        cloneView.fitWidthProperty().bind(board.widthProperty().divide(1.1*8.99));
        getChildren().add(cloneView);
        isDragging=!isDragging;
    }
    private void movePiece(int index, int newIndex) {
        ImageView piece=(ImageView) buttonTiles[index].getGraphic();
        buttonTiles[index].setGraphic(null);
        piece.setVisible(true);
        buttonTiles[newIndex].setGraphic(piece);
        System.out.println(newIndex);
        getChildren().remove(cloneView);
        buttonTiles[index].setSelected(false);
        moves.add(new Move(idkwhattocalllthis.getPieces()[index],index,newIndex));
        idkwhattocalllthis.playMove(moves.get(moveCounter++));
        System.out.println(idkwhattocalllthis.toFEN());
        clearPossibleMoves();
    }
}
