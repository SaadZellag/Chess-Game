import javafx.application.Application;
import javafx.stage.Stage;

public class HelloWorld extends Application {
    public static void main(String[] args) {
        System.out.println("This Project Works");
        Application.launch();
    }

    @Override
    public void start(Stage primaryStage){
        primaryStage.show();
    }
}
