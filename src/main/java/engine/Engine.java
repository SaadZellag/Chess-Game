package engine;

import GUI.GUI;
import game.Board;
import game.ChessUtils;
import game.Move;
import game.Piece;

import java.io.File;
import java.util.Arrays;

public class Engine {

    private static final String OS = System.getProperty("os.name").toLowerCase();

    private static boolean isWindows() {
        return OS.contains("win");
    }

    private static boolean isMac() {
        return OS.contains("mac");
    }

    private static boolean isUnix() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    static {
        String fileName = null;
        if (isWindows()) {
            fileName = "jnilib.dll";
        } else if (isUnix()) {
            fileName = "jnilib.so";
        } else {
            throw new IllegalStateException("Unsupported OS: " + OS);
        }
        String path = Engine.class.getClassLoader().getResource(fileName).getPath();
        System.load(path);
    }

    private static native void init();

    static {
        init();
    }


    public static native void newGame();

    public static native void setDifficulty(double difficulty);

    public static MoveResult getBestMove(Board board, long timeLeft) {
        String[] result = getBestMove(board.toFEN(), (long)(timeLeft * 0.05)).split(" ");

        if (result.length != 2) {
            throw new IllegalStateException("Received unknown String from native method: " + Arrays.toString(result));
        }
        System.out.println("Engine received: " + board.toFEN() + " | " + timeLeft + "ms");
        System.out.println("Engine plays: " + result[0]);

        int initialPosition = ChessUtils.algebraicToIndex(result[0].substring(0, 2));
        Piece pieceMoved = board.getPieces()[initialPosition];

        return new MoveResult(
            ChessUtils.UCIToMove(result[0], pieceMoved),
                Integer.parseInt(result[1]) / 2000.0 + 0.5
        );
    }

    private static native String getBestMove(String FEN, long timeMs) throws IllegalArgumentException;

}
