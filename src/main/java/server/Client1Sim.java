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
public class Client1Sim {
    public static void main(String[] args) {
        Client a = new Client("localhost");
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);
        Move rm;
        Scanner in = new Scanner(System.in);

        for (; ; ) {
            if (a.currentTurn() == 0) {
                System.out.println("PLAY:");
                if (in.nextInt() == 1) {
                    a.sendMove(m);
                    System.out.println(a.receiveMove());
                }
            }
        }
    }
}