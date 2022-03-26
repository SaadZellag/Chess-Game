package server;

import engine.internal.pieces.Pawn;
import game.Move;
import game.Piece;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import static game.PieceType.PAWN;

public class Client {
    private static final int PORT = 6969;
    public int playerID;
    private int movesPlayed;
    private Socket clientSocket;
    private DataOutputStream out;
    private DataInputStream in;
    private ObjectOutputStream objOut;
    private ObjectInputStream objIn;

    public String getHost() {
        ArrayList<String> hostArray = new MulticastFinder().multicast();
        if (hostArray.size() > 1) {
            //TODO: Let player choose which host they want
            return hostArray.get(0); //This is temporary
        } else {
            return hostArray.get(0);
        }
    }

    public Client(int source) {
        System.out.println("--- Client ---");
        try {
            //0 = run from host and 1 = run from external client
            String hostIP;
            if (source == 0) {
                hostIP = getHost();
            } else {
                hostIP = "localhost";
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

    public static void main(String[] args) {
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);

        Scanner s = new Scanner(System.in);
        System.out.println("0 for host 1 for client");
        int o = s.nextInt();

        Client c = new Client(o);
        c.sendMove(m);
    }
}
