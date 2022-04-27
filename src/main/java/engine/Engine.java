package engine;

import game.Board;
import game.ChessUtils;
import game.Piece;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Engine {


    private static URL getResource(String fileName) {
        return Engine.class.getClassLoader().getResource(fileName);
    }

    /*
    * Since the loading of libraries (such as the jeffrey binary) is handled by the system
    * libjnilib can't remain inside the jar as OS will not look into it. This method
    * writes out the dll to a temp folder and loads it from there.
    */
    private static void loadJarDll(String name) throws IOException {
        File origin = new File(Engine.class.getClassLoader().getResource(name).getFile());
        File destination = new File(System.getProperty("java.io.tmpdir") + "/" + name);
        FileUtils.copyFile(origin, destination);
        System.load(destination.getAbsolutePath());
    }

    //0 means filename does not represent a dir, 1 means it does
    private static String loadDatabases(String name, boolean isDirectory) throws IOException {
        File origin = new File(Engine.class.getClassLoader().getResource(name).getFile());
        String destination = System.getProperty("java.io.tmpdir") + "/" + name;
        File dest = new File(destination);
        if (isDirectory) {
            FileUtils.copyDirectory(origin, dest);
            return dest.getAbsolutePath();
        }
        FileUtils.copyFile(origin, dest);
        return dest.getAbsolutePath();
    }


    private static final double PERCENTAGE_USAGE = 0.04;

    private Engine() {}

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
        System.out.println("[INFO]: Loading engine...");
        String fileName;
        if (isWindows()) {
            fileName = "jnilib.dll";
        } else if (isUnix()) {
            fileName = "libjnilib.so";
        } else if (isMac()) {
            fileName = "libjnilib.dylib";
        } else {
            throw new IllegalStateException("Unsupported OS: " + OS);
        }
        try {
            loadJarDll(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Could not load unpacked engine from filesystem.");
        }
        System.out.println("[INFO]: Engine has finished loading");
    }

    static {
        init();

        // Download openings from https://www.mediafire.com/file/123ctlm16x1v51w/d-corbit-v02-superbook.abk.rar/file
        // Download endgames from https://chess.massimilianogoi.com/download/tablebases/

        String path = null;
        try {
            path = loadDatabases("d-corbit-v02-superbook.abk", false);
            if (path.startsWith("/") && isWindows()) {
                // Weird bug for windows where it starts with /C:/...
                path = path.substring(1);
            }
            setOpeningBook(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            path = loadDatabases("tablebases/3-4-5", true);
            if (path.startsWith("/") && isWindows()) {
                // Weird bug for windows where it starts with /C:/...
                path = path.substring(1);
            }
            addEndGameTable(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static Future<MoveResult> currentSearch = null;

    public static Future<MoveResult> getBestMove(Board board, long timeLeft) {
        checkSearchStatus();
        currentSearch = executor.submit(() -> {
            String[] result = getBestMove(board.toFEN(), (long) (timeLeft * PERCENTAGE_USAGE)).split(" ");

            if (result.length != 2) {
                throw new IllegalStateException("Received unknown String from native method: " + Arrays.toString(result));
            }

            int initialPosition = ChessUtils.algebraicToIndex(result[0].substring(0, 2));
            Piece pieceMoved = board.getPieces()[initialPosition];

            return new MoveResult(
                    ChessUtils.UCIToMove(result[0], pieceMoved),
                    Integer.parseInt(result[1]) / 2000.0 + 0.5
            );
        });
        return currentSearch;
    }

    public static Future<MoveResult> getCurrentSearch() {
        return currentSearch;
    }

    public static boolean cancelCurrentSearch() {
        stopCurrentSearch();
        if(currentSearch==null){
            return false;
        }
        return currentSearch.cancel(true);
    }

    private static void checkSearchStatus() {
        if (currentSearch != null && !currentSearch.isDone()) {
            throw new IllegalCallerException("Cannot call another search while one is pending");
        }
    }


    private static native void init();

    public static native void setDifficulty(double difficulty);

    private static native String getBestMove(String FEN, long timeMs) throws IllegalArgumentException;

    private static native void stopCurrentSearch();

    private static native void setOpeningBook(String path);

    private static native void addEndGameTable(String path);

}
