package server;

import game.Move;
import game.Piece;

import java.net.SocketException;
import java.util.Scanner;

import static game.PieceType.PAWN;

public class BlackSim {

    public static void main(String[] args) {
        Client a = new Client("localhost"); // Ip of host --> should the remote address normally
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);
        Turn rt;
        Scanner in = new Scanner(System.in);

        while (true) {
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
