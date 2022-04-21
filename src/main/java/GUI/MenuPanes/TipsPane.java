package GUI.MenuPanes;

import GUI.CustomButton;
import GUI.GamePane;
import game.Piece;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import static GUI.GUI.TRANSITION_DURATION;
import static GUI.GUI.formatStandardText;

public class TipsPane extends MenuPane {
    TipsPane(){//TODO the current layout is garbage, might remake it by using a MenuPane, rather than extending it
        UPPER_TEXT.setText("TIPS");

        final Label CLICK_ON_PIECE= new Label("CLICK ON A PIECE TO VIEW HOW IT MOVES");
        formatStandardText(CLICK_ON_PIECE,heightProperty(),30);
        TipsChessBoardPane chessBoardPane= new TipsChessBoardPane(heightProperty().divide(1.5));

        ToggleButton[] b=chessBoardPane.buttons;
        Piece[] p = chessBoardPane.internalBoard.getPieces();


        for(int i=0;i<b.length;i++){
            if(p[i]!=null){
                int finalI = i;
                b[i].setOnAction(e->{
                    for(ToggleButton button:b){
                        button.setSelected(false);
                    }
                    b[finalI].setSelected(true);
                    boolean isWhite=p[finalI].isWhite;

                    switch (p[finalI].type) {//todo
                        case PAWN -> {
                            System.out.println("pawn" + isWhite);
                        }
                        case BISHOP -> {
                            System.out.println("bishop"+ isWhite);
                        }
                        case KNIGHT -> {
                            System.out.println("KNIGHT"+ isWhite);
                        }
                        case ROOK -> {
                            System.out.println("Rook"+ isWhite);
                        }
                        case QUEEN -> {
                            System.out.println("queen"+ isWhite);
                        }
                        case KING -> {
                            System.out.println("king"+ isWhite);
                        }
                    }
                });
            }
        }


        nextSceneButton = new CustomButton(heightProperty(),"RULES",12);
        nextSceneButton2 = new CustomButton(heightProperty(),"BEGINNER\n     TIPS",12);

        VBox buttonsPane= new VBox(nextSceneButton,nextSceneButton2);
        buttonsPane.setAlignment(Pos.CENTER);
        GridPane.setHgrow(buttonsPane,Priority.ALWAYS);

        GridPane grid = new GridPane();
//        grid.setGridLinesVisible(true);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(45);
        col1.setHalignment(HPos.LEFT);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(55);
        col2.setHalignment(HPos.CENTER);

        grid.getColumnConstraints().addAll(col1, col2);

        grid.add(buttonsPane,0,1);
        grid.addColumn(1,CLICK_ON_PIECE,chessBoardPane);

        MIDDLE_PANE.getChildren().add(grid);

        heightProperty().addListener(e-> {
            buttonsPane.setSpacing(heightProperty().divide(23).doubleValue());
        });
    }
    @Override
    public void playTransitions(){
        centerTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(1)));
        centerTransition.setFromValue(0);
        centerTransition.setToValue(1);

        topTransition.setDuration(TRANSITION_DURATION.add(Duration.seconds(1)));
        topTransition.setFromValue(0);
        topTransition.setToValue(1);

        topTransition.play();
        centerTransition.play();
    }

    @Override
    public GamePane nextMenu() {//Rules Pane
        return new BrowserPane("RULES","Rules.htm");
    }

    @Override
    public GamePane nextMenu2() {//BEGINNER TIPS
        return new BrowserPane("BEGINNER TIPS","ChessTips.htm");
    }

    @Override
    public GamePane previousMenu() {//Main menu
        return new MainMenuPane();
    }
}
