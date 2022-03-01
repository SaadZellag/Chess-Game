import com.sun.prism.paint.Color;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class HelloWorld extends Application {
    public static void main(String[] args) {
        System.out.println("This Project Works");
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage){
        Pane p= new Pane();

        Button b= new Button();
//        b.setBackground(null);
        p.getChildren().add(b);
        Scene scene = new Scene(p,500,500);
        p.setStyle("-fx-background-color: grey");
        b.setGraphic(new ImageView("https://www6.lunapic.com/editor/premade/transparent.gif"));
        b.setOnAction(e-> System.out.println("pressed"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
