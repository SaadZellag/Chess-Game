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

    //How much time left in milliseconds
    public long getTimeLeft() {return timeLeft;}

    //How many minutes left, disregarding the seconds and milliseconds
    public long getMinutesLeft() {
        return timeLeft / 60000;
    }

    //How many seconds left, disregarding the minutes and milliseconds
    public long getSecondsLeft() {
        return (timeLeft / 1000) % 60;
    }

    //How many milliseconds left, disregarding the minutes and seconds
    public long getMillisLeft() {
        return timeLeft % 1000;
    }
}
