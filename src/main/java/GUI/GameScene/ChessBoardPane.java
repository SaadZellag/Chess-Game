package GUI.GameScene;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class ChessBoardPane extends Pane{
    GridPane board= new GridPane();
    StackPane[] tileStacks= new StackPane[64];
    ToggleButton[] buttonTiles= new ToggleButton[64];
    boolean isDragging=false;
    int X_DRAGGING_OFFSET=50;
    int Y_DRAGGING_OFFSET=50;
    ImageView cloneView;


    Image boardImage = new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    BackgroundImage bImage = new BackgroundImage(boardImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(board.getWidth(), board.getHeight(), true, true, true, false));
    Background backGround = new Background(bImage);
    final String TOGGLED_COLOR = "red";
    final String UNTOGGLED_COLOR = "blue";
    final double OPACITY=0.5;

    final ImageView[] W_PAWN= new ImageView[8];
    final ImageView[] W_ROOK= new ImageView[2];
//    final Image W_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image W_KNIGHT= new Image("https://images-na.ssl-images-amazon.com/images/I/61tC4kGj66L.jpg");
    final Image W_BISHOP= new Image("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSL-v1Riij2HwrteDcFbTqG5aD21gRrn5lHcQ&usqp=CAU");
    final Image W_QUEEN= new Image("https://images.squarespace-cdn.com/content/5edb32112cb3cc498e15a24d/1592278746395-AEX09Z0GJDHC3AFLV7AL/Squareprint+93+Logo+final-01.png?content-type=image%2Fpng");
    final Image W_KING= new Image("http://s.thestreet.com/files/tsc/v2008/photos/contrib/uploads/28205704-4d5f-11e8-a2db-e76c960a6c05.png");
    final Image B_PAWN= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_ROOK= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_KNIGHT= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_BISHOP= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_QUEEN= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));
    final Image B_KING= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/Board.jpg"));


    public ChessBoardPane(ReadOnlyDoubleProperty binding){
        getChildren().add(board);
        board.setAlignment(Pos.CENTER);
        board.setBackground(backGround);
//        board.setStyle("-fx-background-color:green");

        //Create chess board
        for (int i=0;i<8;i++){
            for (int j=0;j<8;j++){
                int index= i+8*j;
                //TileStack properties
                final double RATIO=8.99;
                tileStacks [index]= new StackPane();
                tileStacks [index].setAlignment(Pos.CENTER);
//                tileStacks[index].setMinHeight(board.getMinHeight()/RATIO);
//                tileStacks[index].setMinWidth(tileStacks[index].getMinHeight());
//                tileStacks[index].prefWidthProperty().bind(board.widthProperty().divide(RATIO));
//                tileStacks[index].prefHeightProperty().bind(board.widthProperty().divide(RATIO));

                //ButtonTile properties
                buttonTiles[index]=new ToggleButton();
                buttonTiles[index].prefWidthProperty().bind(board.widthProperty().divide(RATIO));
                buttonTiles[index].prefHeightProperty().bind(board.widthProperty().divide(RATIO));
//                buttonTiles[index].maxHeightProperty().bind(board.widthProperty().divide(RATIO));
//                buttonTiles[index].maxWidthProperty().bind(board.widthProperty().divide(RATIO));
                buttonTiles[index].setOpacity(OPACITY);
                buttonTiles[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                buttonTiles[index].setPadding(Insets.EMPTY);
//                buttonTiles[index].setDisable(true);

                board.add(tileStacks[index],i,j);
                tileStacks [index].getChildren().add(buttonTiles[index]);
                buttonTiles[index].setOnMouseDragged(e->tileDragged(index,e));
                buttonTiles[index].setOnMouseReleased(e->tileReleased(index,e));
                buttonTiles[index].setOnAction(e-> tileClicked(index));
                buttonTiles[index].setOnDragEntered(e-> System.out.println(index));

            }

            W_PAWN[i]= new ImageView(new Image("https://upload.wikimedia.org/wikipedia/commons/d/de/Windows_live_square.JPG"));
            W_PAWN[i].setPreserveRatio(true);
            W_PAWN[i].setMouseTransparent(true);
            W_PAWN[i].fitHeightProperty().bind(board.widthProperty().divide(1.1*8.99));
//            tileStacks[48+i].getChildren().add(W_PAWN[i]);
            buttonTiles[48+i].setGraphic(W_PAWN[i]);
            buttonTiles[48+i].setDisable(false);
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

    public void tileClicked(int index){
        System.out.println(index);
        if(buttonTiles[index].isSelected())
            buttonTiles[index].setStyle("-fx-background-color:"+TOGGLED_COLOR);
        else
            buttonTiles[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);

    }
    public void tileDragged(int index, MouseEvent e){//created copy of piece image that can be dragged around the screen
        if (tileStacks[index].getChildren().size()!=1){
            if (!isDragging){
                buttonTiles[index].setSelected(true);
                buttonTiles[index].setStyle("-fx-background-color:"+TOGGLED_COLOR);
                tileStacks[index].getChildren().get(1).setVisible(false);
                Image cloneImage= ((ImageView) tileStacks[index].getChildren().get(1)).getImage();
                cloneView = new ImageView(cloneImage);
                cloneView.setPreserveRatio(true);
                cloneView.setMouseTransparent(true);
                cloneView.fitWidthProperty().bind(board.widthProperty().divide(1.1*8.99));
                getChildren().add(cloneView);
                isDragging=!isDragging;
            }
            cloneView.relocate(e.getSceneX()-X_DRAGGING_OFFSET,e.getSceneY()-Y_DRAGGING_OFFSET);
        }

    }
    public void tileReleased(int index, MouseEvent e){
        Node releasedLocation=e.getPickResult().getIntersectedNode().getParent();
        isDragging=false;
        if(tileStacks[index].getChildren().size()!=1){
            if (releasedLocation instanceof StackPane&&((StackPane) releasedLocation).getChildren().size()==1){
                int newIndex=GridPane.getRowIndex(releasedLocation)*8+GridPane.getColumnIndex(releasedLocation);
                ImageView piece=(ImageView) tileStacks[index].getChildren().remove(1);
                piece.setVisible(true);
                tileStacks[newIndex].getChildren().add(piece);
                System.out.println(newIndex);
                getChildren().remove(cloneView);
                buttonTiles[index].setStyle("-fx-background-color:"+UNTOGGLED_COLOR);
                buttonTiles[index].setSelected(false);
            }
            else{
                tileStacks[index].getChildren().get(1).setVisible(true);
                getChildren().remove(cloneView);
            }
        }
    }
}
