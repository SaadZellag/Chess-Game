package GUI.MenuPanes;

import GUI.GamePane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static GUI.GUI.*;

public class BrowserPane extends MenuPane{

    public BrowserPane(String subtext, String url){
        webView.setEffect(null);
        UPPER_TEXT.setText("TIPS");
        UPPER_SUBTEXT.setText("  "+subtext);
        MIDDLE_PANE.maxWidthProperty().bind(widthProperty().divide(1.3));
        MIDDLE_PANE.getChildren().add(webView);
        MIDDLE_PANE.setEffect(glowEffect(Color.GOLD,Color.RED));
//        webView.getEngine().setJavaScriptEnabled(true);
        webView.getEngine().load(getResource(url));
    }
    @Override
    public void playTransitions(){
        centerTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(0.5)));
        centerTransition.setFromValue(0);
        centerTransition.setToValue(1);

        topTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(0.5)));
        topTransition.setFromValue(0);
        topTransition.setToValue(1);

        topTransition.play();
        centerTransition.play();
    }
    @Override
    public GamePane previousMenu(){
        webView.setEffect(new ColorAdjust(-1,-1,-1,1));
        webView.getEngine().load("");
        return new TipsPane();
    }
}
