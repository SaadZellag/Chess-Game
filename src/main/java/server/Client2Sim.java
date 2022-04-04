package server;

import game.Move;
import game.Piece;

import java.util.Scanner;

import static game.PieceType.PAWN;

public class Client2Sim {

    public static void main(String[] args) {
        Client a = new Client("localhost"); // Ip of host --> should the remote address normally
        Piece p = new Piece(false, PAWN);
        Move m = new Move(p, 12, 18);
        Move rm;
        Scanner in = new Scanner(System.in);

        while (true) {
            if (a.currentTurn() == 1) {
                System.out.println("PLAY:");
                if (in.nextInt() == 1) {
                    a.sendMove(m);
                    rm = a.receiveMove();
                    System.out.println(rm);
                }
            }
        }
    }
}
