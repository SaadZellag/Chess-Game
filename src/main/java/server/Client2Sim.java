package server;

import game.Move;
import game.Piece;

import java.util.Scanner;

import static game.PieceType.PAWN;

public class Client2Sim {

    public static void main(String[] args) {
        Client a = new Client(1);
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);
        Move rm;
        Scanner in = new Scanner(System.in);

        for (; ; ) {
            if (a.isTurn()) {
                System.out.println("PLAY:");
                if (in.nextInt() == 1) {
                    a.sendMove(m);
                    a.receiveMove();
                }
            }
        }
    }
}
