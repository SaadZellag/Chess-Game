package server;

import game.Move;

import java.io.Serializable;

public class Turn implements Serializable {
    public Move move;
    public int movesPlayed;
    public long timeLeft;

    public Turn(Move move, int movesPlayed, long timeLeft) {
        this.move = move;
        this.movesPlayed = movesPlayed;
        this.timeLeft = timeLeft;
    }

    public Move getMove() {return move;}

    public int getMovesPlayed() {
        return movesPlayed;
    }
}
