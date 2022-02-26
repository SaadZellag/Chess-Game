package GUI;

import game.Move;
import game.Piece;
import game.PieceType;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

public class MoveHistoryField extends StackPane {
    Image settingsImage= new Image(getClass().getClassLoader().getResourceAsStream("GUIResources/RoundTextArea.png"));
    ImageView imageView= new ImageView(settingsImage);
    ScrollPane scrollPane= new ScrollPane();
    VBox vBox= new VBox();
    public MoveHistoryField(ReadOnlyDoubleProperty binding){
        prefSizePropertyBind(binding);
        getChildren().addAll(imageView,scrollPane);
        setAlignment(Pos.CENTER);
        scrollPane.setStyle("-fx-background:transparent;-fx-background-color:transparent");
        scrollPane.prefViewportWidthProperty();


        scrollPane.setContent(vBox);
        for (int i = 0; i < 100; i++) {
            vBox.getChildren().add(displayMove(new Move(new Piece(false, PieceType.PAWN),1,3)));
        }

    }
    private Text displayMove(Move move){
        return new Text(move.piece.type.toString()+move.initialLocation+move.finalLocation);

    }
    public void prefSizePropertyBind (ReadOnlyDoubleProperty binding){
        imageView.setPreserveRatio(true);
        imageView.fitHeightProperty().bind(binding);
        scrollPane.setPrefSize(11,11);
        scrollPane.prefWidthProperty().bind(binding.divide(5));
        scrollPane.prefHeightProperty().bind(binding.divide(5));

    }
}
