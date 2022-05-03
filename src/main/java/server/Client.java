package server;
import game.Move;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private static final int PORT = 6969;
    String host;
    public int playerID;
    private int movesPlayed;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public static ArrayList<String> getHostIPs() {
        return new MulticastFinder().multicast();
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Client(String host) {
        System.out.println("--- Client ---");
        try {
            //1 = run from host and 0 = run from external client
            String hostIP = "localhost";
            if (!host.equals("localhost")) {
                hostIP = host;
            }
            clientSocket = new Socket(hostIP, PORT);
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            out = new ObjectOutputStream(outputStream);
            in = new ObjectInputStream(inputStream);
            playerID = in.readInt();
            System.out.println("Connected as player " + playerID);
        } catch (IOException ignore) {
            System.out.println("joined non existent room");
        }
    }

    public void sendMove(Move move) {
        try {
            out.writeObject(move);
            out.flush();
            System.out.println("Sent move " + move.toString() + " to server.");
        } catch (IOException e) {
            System.out.println("Sent move to closed server. OK if opponent quit mid-game.");
        }
        //return in.readUTF();
    }

    public void endGame() {
        try {
            out.writeObject(null);
            out.flush();
            in.close();
            out.close();
            clientSocket.close();
            System.out.println("Sent end signal to server.");
        } catch (IOException | NullPointerException ignore) {
//            e.printStackTrace();
        }
    }

    public Turn receiveTurn() throws SocketException{
        try {
            Turn rt = (Turn) in.readObject();

            if (rt == null) {
                System.out.println("Received end game signal.");
                in.close();
                out.close();
                clientSocket.close();
                return null;
            }

            System.out.println("Player " + playerID + " received " + rt.getMove() + " | Total turns: " + rt.getMovesPlayed());
            return rt;
        }catch(IOException | ClassNotFoundException ignore){
//            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            out.close();
            in.close();
            clientSocket.close();
            System.out.println("Successfully closed client socket.");
        } catch (IOException e) {
            System.out.println("Exception in client close method.");
        }
    }
}
