package server;
/*
    This is another Host type of class, just following
    guidelines for a two player game instead of simply a server.
*/

import game.Move;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameServer {

    private final int PORT = 6969;
    private ServerSocket ss;
    private int playersCon;
    // 0 = white's turn to play and 1 == black's turn
    int movesPlayed = 0;
    private ServerSideConnection whiteConnection;
    private ServerSideConnection blackConnection;
    private Move whiteMove;
    private Move blackMove = null;
    private ArrayList<Thread> threadList = new ArrayList<>();
    Thread beacon;

    public GameServer() {
        System.out.println("---Game Server---");
        playersCon = 0;
        movesPlayed = 0;

        try {
            beacon = new Thread(new MulticastServer());
            beacon.start();
            ss = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accept() {
        try {
            System.out.println("Waiting for connection...");
            while (playersCon < 2) {
                Socket s = ss.accept();
                playersCon++;
                System.out.println("Player " + playersCon +" has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, playersCon);
                if (playersCon == 1) {
                    whiteConnection = ssc;
                } else {
                    blackConnection = ssc;
                }
                threadList.add(new Thread(ssc));
                //Thread t = new Thread(ssc);
                //t.start();
            }
            System.out.println("All players connected.");
            for (Thread t : threadList) {
                t.start();
            }
            beacon.interrupt();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private ObjectInputStream objIn;
        private ObjectOutputStream objOut;
        int playerID;

        public ServerSideConnection(Socket s, int id) {
            this.socket = s;
            this.playerID = id;

            try {
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());
                objIn = new ObjectInputStream(socket.getInputStream());
                objOut = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                System.out.println("IO error from server side connection constructor");
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                //Send each player their ID
                out.writeInt(playerID);
                out.flush();

                //Right now the first player to connect is white and the second is black
                    while (true) {

                        //ID 1 = first player connected so white
                        if (playerID == 1) {
                            whiteMove = (Move) objIn.readObject();
                            System.out.println("White played move " + movesPlayed);
                            movesPlayed++;
                            blackConnection.sendBackMove(whiteMove, movesPlayed);
                        } else {
                            blackMove = (Move) objIn.readObject();
                            System.out.println("Black played move " + movesPlayed);
                            movesPlayed++;
                            whiteConnection.sendBackMove(blackMove, movesPlayed);
                        }
                    }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public void sendBackMove(Move m, int mp) {
            try {
                objOut.writeObject(m);
                out.write(mp);
                objOut.flush();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.accept();
    }
}
