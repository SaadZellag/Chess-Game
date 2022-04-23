package server;
import game.Move;
import game.Piece;

import java.net.SocketException;
import java.util.Scanner;
import static game.PieceType.PAWN;
/*
           --Very basic test.--
    Server must be running before running
    any of the test clients.

 */
public class WhiteSim {
    public static void main(String[] args) {
        Client a = new Client("localhost");
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);
        Turn rt;
        Scanner in = new Scanner(System.in);
        int moves = 0;

        for (; ; ) {
            //If first move don't listen for other move
            if (moves == 0) {
                System.out.println("PLAY:");
                if (in.nextInt() == 1) {
                    a.sendMove(m);
                } else {
                    a.endGame();
                    break;
                }
                moves++;
            }

            try {
                rt = a.receiveTurn();
                long wt = rt.getWhiteTimeLeft();
                long bt = rt.getBlackTimeLeft();
                System.out.println(Turn.millisToMinutesLeft(wt) + ":" + Turn.millisToSecondsLeft(wt) + "." + Turn.millisLeft(wt));
                System.out.println(Turn.millisToMinutesLeft(bt) + ":" + Turn.millisToSecondsLeft(bt) + "." + Turn.millisLeft(bt));
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            //null turn means end game, shutdown handled in the client class
            if (rt == null) {
                break;
            }

            System.out.println("PLAY:");
            if (in.nextInt() == 1) {
                a.sendMove(m);
                System.out.println(rt.getMove());
            } else {
                a.endGame();
                break;
            }
        }
    }
}
