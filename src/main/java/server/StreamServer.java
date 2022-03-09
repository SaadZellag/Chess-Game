package server;

import game.Board;
import java.net.*;
import java.io.*;

public class StreamServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    String fen;

    StreamServer(String fen) {
        this.fen = fen;
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);

        while (true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public static void main(String[] args) throws IOException {

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        Board board = new Board(fen);

        StreamServer server = new StreamServer(fen);
        server.start(6969);
    }
}