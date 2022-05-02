package engine;

import GUI.GUI;
import game.Board;
import game.ChessUtils;
import game.Move;
import game.Piece;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Engine {

    private static URL getResource(String fileName) {
        return Engine.class.getClassLoader().getResource(fileName);
    }

    private static void writeStreamToFile(InputStream inputStream, File outputFile) throws IOException {
        try {
            Files.copy(inputStream, outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            inputStream.close();
        }
    }

    public static void copyFile(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath());
    }

    //Thanks to kayz1
    // https://stackoverflow.com/a/26214647
    private static void copyFolder(File source, File destination){
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (Exception e) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /*
    * Since the loading of libraries (such as the jeffrey binary) is handled by the system
    * libjnilib can't remain inside the jar as OS will not look into it. This method
    * writes out the dll to a temp folder and loads it from there.
    */
    private static void loadJarDll(String name) throws IOException {
        InputStream in = Engine.class.getClassLoader().getResourceAsStream(name);
        File temp = new File(System.getProperty("java.io.tmpdir"), name);
        writeStreamToFile(in, temp);
        System.load(temp.getAbsolutePath());
        in.close();
    }

    private static String loadDatabases(String name, boolean isDirectory) throws IOException {
        InputStream in = Engine.class.getClassLoader().getResourceAsStream(name);
        File dest = new File(System.getProperty("java.io.tmpdir") + "/" + name);
        if (!dest.canRead()) {
            dest.getParentFile().mkdirs();
            dest.createNewFile();
        }
        writeStreamToFile(in, dest);
        in.close();
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

    private static void loadData() {
        // Download openings from https://www.mediafire.com/file/123ctlm16x1v51w/d-corbit-v02-superbook.abk.rar/file
        // Download endgames from https://chess.massimilianogoi.com/download/tablebases/

        String openings = "openings/d-corbit-v02-superbook.abk";
        if (openings != null) {
            String path = null;
            try {
                path = loadDatabases(openings, false);
            } catch (IOException e) {
                System.out.println("Failed loading openings.");
                e.printStackTrace();
            }
            if (path.startsWith("/") && isWindows()) {
                // Weird bug for windows where it starts with /C:/...
                path = path.substring(1);
            }
            setOpeningBook(path);
        }

        String endgames = "tablebases/3-4-5.zip";
        if (endgames != null) {
            String path = null;
            try {
                path = loadDatabases(endgames, false);
            } catch (IOException e) {
                System.out.println("Failed loading endgames.");
                e.printStackTrace();
            }
            if (path.startsWith("/") && isWindows()) {
                // Weird bug for windows where it starts with /C:/...
                path = path.substring(1);
            }
            addEndGameTable(path);
        }
    }

    static {
        init();
        new Thread(Engine::loadData).start();
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

    public static void main(String[] args) {
        try {
            loadDatabases("openings/d-corbit-v02-superbook.abk", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
