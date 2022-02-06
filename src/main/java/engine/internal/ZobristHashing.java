package engine.internal;


import java.util.Random;

// https://www.chessprogramming.org/Zobrist_Hashing
public class ZobristHashing {

    private ZobristHashing() {}

    private static final long[] piece_keys = new long[12*64];
    private static final long white_turn_key;
    private static final long[] castling_keys = new long[4];
    private static final long[] en_passant_keys = new long[8];


    static {
        // Init the hashing keys
        Random r = new Random(2031489);

        for (int i = 0; i < piece_keys.length; i++) {
            piece_keys[i] = r.nextLong();
        }

        white_turn_key = r.nextLong();

        for (int i = 0; i < castling_keys.length; i++) {
            castling_keys[i] = r.nextLong();
        }

        for (int i = 0; i < en_passant_keys.length; i++) {
            en_passant_keys[i] = r.nextLong();
        }
    }

    public static long hashBoard(long[] board) {
        // Every piece
        long hash = 0, mask;
        int j;
        for (int i = 0; i < 12; i++) {
            mask = board[i];
            while ((j = Long.numberOfTrailingZeros(mask)) != 64) {
                hash ^= piece_keys[i*64+j];
                mask &= (mask-1);
            }
        }

        long info = board[BitBoard.GAME_INFO];

        // Is white turn
        hash ^= white_turn_key * (info & GameInfo.IS_WHITE_TURN);

        // Castling
        hash ^= castling_keys[0] * (GameInfo.canWhiteCastleKingSide(info) ? 1 : 0);
        hash ^= castling_keys[1] * (GameInfo.canWhiteCastleQueenSide(info) ? 1 : 0);
        hash ^= castling_keys[2] * (GameInfo.canBlackCastleKingSide(info) ? 1 : 0);
        hash ^= castling_keys[3] * (GameInfo.canBlackCastleQueenSide(info) ? 1 : 0);

        int epts = GameInfo.enPassantTargetSquare(info);
        if (epts > 0) {
            hash ^= en_passant_keys[epts % 8];
        }

        return hash;
    }
}
