package server;
/*
    Server class that receives, handles, and sends back
     input from two clients
*/

import GUI.GameplayPanes.MultiplayerGamePane;
import game.Move;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;

public class GameServer {
    private final int PORT = 6969;
    private final int TIMEOUT = 60000;
    private ServerSocket ss;
    private Socket s;
    private int playersCon;
    // 0 = white's turn to play and 1 == black's turn
    protected int movesPlayed;
    //In milliseconds
    private long whiteTimeLeft, blackTimeLeft, increment;
    private ServerSideConnection whiteConnection, blackConnection;
    private Thread beacon;

    //Game duration in millisecond
    public GameServer(long gameDuration, long increment) {
        this.increment = increment;
        whiteTimeLeft = gameDuration;
        blackTimeLeft = gameDuration;
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
            System.out.println("Exception in accept method.");
            beacon.interrupt();
            return 1;
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
                            while (movesPlayed % 2 == 1) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    System.out.println("Player 1 sleep exception");
                                }
                            }
                            Instant start = Instant.now();
                            Move whiteMove = (Move) in.readObject();
                            Instant end = Instant.now();
                            long turnTime = Duration.between(start, end).toMillis();
                            if (movesPlayed != 0) {
                                whiteTimeLeft -= (turnTime - increment);
                            }
                            if (whiteMove == null) {
                                System.out.println("Closing server on thread " + playerID );
                                if (playersCon > 1) {
                                    blackConnection.sendBackTurn(null);
                                }
                                break;
                            }

                            System.out.println("White played move " + movesPlayed + " --> " + whiteMove + " | in " + turnTime / 60000 + ":" + (turnTime / 1000) % 60 + "." + turnTime % 1000);
                            System.out.println("Remaining time: " + whiteTimeLeft / 60000 + ":" + (whiteTimeLeft / 1000) % 60 + "." + whiteTimeLeft % 1000);
                            System.out.println();
                            movesPlayed++;
                            turn = new Turn(whiteMove, movesPlayed, whiteTimeLeft);
                            blackConnection.sendBackTurn(turn);
                        } else {
                            while (movesPlayed % 2 == 0) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                    System.out.println("Player 2 sleep exception");
                                }
                            }
                            Instant start = Instant.now();
                            Move blackMove = (Move) in.readObject();
                            Instant end = Instant.now();
                            long turnTime = Duration.between(start, end).toMillis();
                            blackTimeLeft -= (turnTime - increment);
                            if (blackMove == null) {
                                System.out.println("Closing server on thread " + playerID);
                                whiteConnection.sendBackTurn(null);
                                break;
                            }
                            System.out.println("Black played move " + movesPlayed + " --> " + blackMove + " | in " + turnTime / 60000 + ":" + (turnTime / 1000) % 60 + "." + turnTime % 1000);
                            System.out.println("Remaining time: " + blackTimeLeft / 60000 + ":" + (blackTimeLeft / 1000) % 60 + "." + blackTimeLeft % 1000);
                            System.out.println();
                            movesPlayed++;
                            turn = new Turn(blackMove, movesPlayed, blackTimeLeft);
                            whiteConnection.sendBackTurn(turn);
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
            } catch (IOException e) {
                Platform.runLater(()-> {//send back to main menu if the match connection was severed mid-game
                    if((GUI.GUI.ROOT.getChildren().get(0) instanceof MultiplayerGamePane)&&(!((MultiplayerGamePane) GUI.GUI.ROOT.getChildren().get(0)).chessBoardPane.gameEnded))
                        ((MultiplayerGamePane) GUI.GUI.ROOT.getChildren().get(0)).nextSceneButton.fire();
                    else
                        e.printStackTrace();
                });
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
        GameServer gs = new GameServer(600000, 5000);
        gs.accept();
    }
}
