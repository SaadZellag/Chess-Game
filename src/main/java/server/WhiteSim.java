package server;
import game.Move;
import game.Piece;
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
                }
                moves++;
            }

            rt = a.receiveTurn();
            System.out.println("PLAY:");
            if (in.nextInt() == 1) {
                a.sendMove(m);
                System.out.println(rt.getMove());
            }
        }
    }
}