package GUI.GameScene;

public class SingleplayerGameScene extends MultiplayerGameScene {

    public SingleplayerGameScene(double width, double height) {
        super(width, height);
        ConfidenceBar confidenceBar= new ConfidenceBar(heightProperty(),whiteIsBottom);
        mainPane.getChildren().add(0,confidenceBar);
        mainPane.setSpacing(9);
//            confidenceBar.moveBar(.5);

    }
    //just add undo/Redo button to the base scene and confidence meter


}
