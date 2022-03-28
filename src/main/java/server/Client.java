package server;

import engine.internal.pieces.Pawn;
import game.Move;
import game.Piece;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import static game.PieceType.PAWN;

public class Client {
    private static final int PORT = 6969;
    String host;
    public int playerID;
    private int movesPlayed;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public static HashMap<String, String> getHostDict() {
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
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
            objOut = new ObjectOutputStream(clientSocket.getOutputStream());
            objIn = new ObjectInputStream(clientSocket.getInputStream());
            playerID = in.readInt();
            System.out.println("Connected as player " + playerID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMove(Move move) {
        try {
            objOut.writeObject(move);
            out.flush();
            System.out.println("Sent move to server.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //return in.readUTF();
    }

    public Move receiveMove() {
        if (playerID == 1 && movesPlayed == 0) {
            movesPlayed++;
            return null;
        }

        try {
            if (in.available() == 1 && objIn.available() == 1) {
                movesPlayed = in.readInt();
                Move rm = (Move) objIn.readObject();
                System.out.println("Player " + playerID + " received move from opposing player.");
                return rm;
            }
        } catch(IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

     public boolean isTurn() {
         movesPlayed++;
        //Turn = 0 for player 1 and 1 for player 2
         return movesPlayed == playerID;
     }

    public void stopConnection() {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
