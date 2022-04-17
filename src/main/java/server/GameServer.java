package server;
/*
    Server class that receives, handles, and sends back
     input from two clients
*/

import game.Move;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class GameServer {

    private final int PORT = 6969;
    private final int TIMEOUT = 12000;
    private ServerSocket ss;
    private Socket s;
    private int playersCon;
    // 0 = white's turn to play and 1 == black's turn
    int movesPlayed;
    private ServerSideConnection whiteConnection;
    private ServerSideConnection blackConnection;
    Thread beacon;

    public GameServer() {
        System.out.println("---Game Server---");
        playersCon = 0;
        movesPlayed = 0;

        try {
            beacon = new Thread(new MulticastServer());
            beacon.start();
            ss = new ServerSocket(PORT);
            ss.setSoTimeout(TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int accept() {
        try {
            System.out.println("Waiting for connection...");
            while (playersCon < 2) {
                try {
                    s = ss.accept();
                } catch (SocketTimeoutException e) {
                    System.out.println("Timeout reached. Closing socket and multicast server.");
                    beacon.interrupt();
                    ss.close();
                    return 1;
                }
                playersCon++;
                System.out.println("Player " + playersCon +" has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, playersCon);
                if (playersCon == 1) {
                    whiteConnection = ssc;
                } else {
                    blackConnection = ssc;
                }
                //threadList.add(new Thread(ssc));
                Thread t = new Thread(ssc);
                t.start();
            }

            if (playersCon == 2) {
                System.out.println("All players connected.");
            }

            beacon.interrupt();
            System.out.println("SERVER: Shut down multicast server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        int playerID;

        public ServerSideConnection(Socket s, int id) {
            this.socket = s;
            this.playerID = id;

            try {
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                in = new ObjectInputStream(inputStream);
                out = new ObjectOutputStream(outputStream);
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

                //To terminate the game, call end game from either client
                    while (true) {
                        //ID 1 = first player connected so white
                        Turn turn;

                        if (playerID == 1) {
                            Move whiteMove = (Move) in.readObject();
                            if (whiteMove == null) {
                                System.out.println("Closing server on thread " + playerID );
                                if (playersCon > 1) {
                                    blackConnection.sendBackTurn(null);
                                }
                                break;
                            }
                            System.out.println("White played move " + movesPlayed + " | " + whiteMove);
                            movesPlayed++;
                            turn = new Turn(whiteMove, movesPlayed);
                            blackConnection.sendBackTurn(turn);
                            System.out.println("Sent " + whiteMove + " | " + movesPlayed);

                        } else {
                            Move blackMove = (Move) in.readObject();
                            if (blackMove == null) {
                                System.out.println("Closing server on thread " + playerID);
                                whiteConnection.sendBackTurn(null);
                                break;
                            }
                            System.out.println("Black played move " + movesPlayed + " | " + blackMove);
                            movesPlayed++;
                            turn = new Turn(blackMove, movesPlayed);
                            whiteConnection.sendBackTurn(turn);
                            System.out.println("Sent: " + blackMove + " | " + movesPlayed);
                        }
                    }

                    whiteConnection.closeConnection();
                    if (playersCon > 1) {
                        blackConnection.closeConnection();
                    }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("IO exception from run method. This is normal if recently shut down.");
            }
        }

        public void sendBackTurn(Turn turn) {
            try {

                if (turn == null) {
                    out.writeObject(null);
                    out.flush();
                    System.out.println("Sent end game signal to client.");
                    return;
                }

                out.writeObject(turn);
                out.flush();
                System.out.println("This was the move sent: " + turn.getMove());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void closeConnection() {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("IO exception from closeConnection method.");
            }
        }
    }

    public void closeSocket() {
        try {
        ss.close();
        s.close();
        } catch (IOException e) {
            System.out.println("IO exception in run method");
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.accept();
    }
}
