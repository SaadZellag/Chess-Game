package engine;

import game.Move;

public class MoveResult {
    public final Move move;
    public final double confidence;

    public MoveResult(Move move, double confidence) {
        this.move = move;
        this.confidence = confidence;
    }
}
