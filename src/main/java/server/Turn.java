package server;

import game.Move;

import java.io.Serializable;

public class Turn implements Serializable {
    public Move move;
    public int movesPlayed;
    private long whiteTimeLeft, blackTimeLeft;

    public Turn(Move move, int movesPlayed, long whiteTimeLeft, long blackTimeLeft) {
        this.move = move;
        this.movesPlayed = movesPlayed;
        this.whiteTimeLeft = whiteTimeLeft;
        this.blackTimeLeft = blackTimeLeft;
    }

    public Move getMove() {return move;}

    public int getMovesPlayed() {
        return movesPlayed;
    }

    //How much time left in milliseconds
    public long getWhiteTimeLeft() {return whiteTimeLeft;}
    public long getBlackTimeLeft() {return blackTimeLeft;}

    //How many minutes left, disregarding the seconds and milliseconds
    public static long millisToMinutesLeft(long millis) {
        return millis / 60000;
    }

    //How many seconds left, disregarding the minutes and milliseconds
    public static long millisToSecondsLeft(long millis) {
        return (millis / 1000) % 60;
    }

    //How many milliseconds left, disregarding the minutes and seconds
    public static long millisLeft(long millis) {
        return millis % 1000;
    }
}
