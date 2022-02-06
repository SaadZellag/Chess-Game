package engine.internal;

import game.ChessUtils;

import java.util.Locale;

import static engine.internal.BitBoard.PAWN;
import static engine.internal.BitBoard.PIECES_TO_INDEX;

public interface BitMove {

    // Move Structure (right to left)
    // Bit 1-6: To index
    // Bit 7-12: From index
    // Bit 13-15: Piece making the move
    // Bit 16-18: Promotion (for pawns)
    // Bit 19-24: En passant target square

    int FROM_INDEX = 6;
    int PIECE_MOVE_INDEX = 12;
    int PROMOTION_INDEX = 15;
    int EN_PASSANT_INDEX = 18;

    int TO = 0b111111;
    int FROM = 0b111111 << FROM_INDEX;
    int PIECE_MOVE = 0b111 << PIECE_MOVE_INDEX;
    int PROMOTION_PIECE = 0b111 << PROMOTION_INDEX;
    int EN_PASSANT_TARGET = 0b111111 << EN_PASSANT_INDEX;


    // Generally for pawns
    static int fromPromotion(int from, int to, int promotion) {
        return to | (from << FROM_INDEX) | (PAWN << PIECE_MOVE_INDEX) | (promotion << PROMOTION_INDEX);
    }

    static int fromEnPassant(int from, int to, int epts) {
        return to | (from << FROM_INDEX) | (PAWN << PIECE_MOVE_INDEX) | (epts << EN_PASSANT_INDEX);
    }

    static int from(int from, int to, int piece) {
        return to | (from << FROM_INDEX) | (piece << PIECE_MOVE_INDEX);
    }

    static int from(int from, int to, int piece, int promotion) {
        return to | (from << FROM_INDEX) | (piece << PIECE_MOVE_INDEX) | (promotion << PROMOTION_INDEX);
    }

    static int toIndex(int move) {
        return move & TO;
    }

    static int fromIndex(int move) {
        return (move & FROM) >>> FROM_INDEX;
    }

    static int pieceMoved(int move) {
        return (move & PIECE_MOVE) >>> PIECE_MOVE_INDEX;
    }

    static int promotionPiece(int move) {
        return (move & PROMOTION_PIECE) >>> PROMOTION_INDEX;
    }

    static int enPassantTarget(int move) {
        return (move & EN_PASSANT_TARGET) >>> EN_PASSANT_INDEX;
    }

    static String moveToAlgebraic(int move) {
        int start = BitMove.fromIndex(move);
        int end = BitMove.toIndex(move);
        int pp = BitMove.promotionPiece(move);
        String promotionType = pp == 0 ? "" : String.valueOf(BitBoard.INDEX_TO_PIECES[6+pp]);
        return ChessUtils.indexToAlgebraic(63-start) + ChessUtils.indexToAlgebraic(63-end) + promotionType;
    }

    static int algebraicToMove(String move) {
        move = move.toLowerCase();
        return BitMove.fromPromotion(
                63 - ChessUtils.algebraicToIndex(move.substring(0, 2)),
                63 - ChessUtils.algebraicToIndex(move.substring(2, 4)),
                move.length() > 4 ? PIECES_TO_INDEX.get(move.charAt(4)) - 6 : 0
        );
    }


}
