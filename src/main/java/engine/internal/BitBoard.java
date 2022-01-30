package engine.internal;

import java.util.Arrays;

public interface BitBoard {

    private static long generateFile(int file) {
        long start = 1;
        for (int i = 0; i < 7; i++) {
            start <<= 8;
            start += 1;
        }
        return start << (7 - file);
    }

    long FILE_A = generateFile(0);
    long FILE_B = generateFile(1);
    long FILE_C = generateFile(2);
    long FILE_D = generateFile(3);
    long FILE_E = generateFile(4);
    long FILE_F = generateFile(5);
    long FILE_G = generateFile(6);
    long FILE_H = generateFile(7);

    long[] FILES = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};


    long RANK_1 = 0b11111111L;
    long RANK_2 = 0b11111111L << 8L;
    long RANK_3 = 0b11111111L << 16L;
    long RANK_4 = 0b11111111L << 24L;
    long RANK_5 = 0b11111111L << 32L;
    long RANK_6 = 0b11111111L << 40L;
    long RANK_7 = 0b11111111L << 48L;
    long RANK_8 = 0b11111111L << 56L;

    long[] RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    // Board structure
    // Single array of long of size 15
    // long[] board = new long[15];
    // Where index:
        // 0:  White Pawns
        // 1:  White Bishops
        // 2:  White Knight
        // 3:  White Rooks
        // 4:  White Queens
        // 5:  White King
        // 6:  Black Pawns
        // 7:  Black Bishops
        // 8:  Black Knight
        // 9:  Black Rooks
        // 10: Black Queens
        // 11: Black King
        // 12: White Pieces
        // 13: Black Pieces
        // 14: All   Pieces

    char[] pieces = {
            'P',
            'B',
            'N',
            'R',
            'Q',
            'K',
            'p',
            'b',
            'n',
            'r',
            'q',
            'k',
    };

    static String toString(long[] board) {
        StringBuilder builder = new StringBuilder();
        char[] pseudoBoard = new char[64];
        Arrays.fill(pseudoBoard, ' ');

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 64; j++) {
                if ((board[i] & 1L << j) != 0) {
                    pseudoBoard[63-j] = pieces[i];
                }
            }
        }

        builder.append("    A   B   C   D   E   F   G   H  \n");
        builder.append("  +---+---+---+---+---+---+---+---+\n");
        for (int i = 0; i < 8; i++) {
            builder.append(8-i);
            builder.append(" | ");
            for (int j = 0; j < 8; j++) {
                builder.append(pseudoBoard[i*8+j]);
                builder.append(" | ");
            }
            builder.append(8-i);
            builder.append("\n  +---+---+---+---+---+---+---+---+\n");
        }
        builder.append("    A   B   C   D   E   F   G   H  \n");

        return builder.toString();
    }

}
