package server;

import game.Move;

import java.io.Serializable;

public class Turn implements Serializable {
    public Move move;
    public int movesPlayed;

    public Turn(Move m, int mp) {
        this.move = m;
        this.movesPlayed = mp;
    }

    public Move getMove() {return move;}

    public int getMovesPlayed() {
        return movesPlayed;
    }
}
