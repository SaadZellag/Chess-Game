package engine.internal;

public interface MoveInfo {

    // Move Info Structure (right to left)
    // Bit 1-3:     Piece Captured
    // Bit 4:       Is En passant
    // Bit 5:       Is King Castle
    // Bit 6:       Is Queen Castle
    // Bit 7:       Is Quiet Move
    // Bit 8-10:    PromotionPiece
    // Bit 11:      Is Double Push

    int EN_PASSANT_INDEX    = 3;
    int KING_CASTLE_INDEX   = 4;
    int QUEEN_CASTLE_INDEX  = 5;
    int QUIET_MOVE_INDEX    = 6;
    int PROMOTION_INDEX     = 7;

    int PIECE_CAPTURED  = 0b111;
    int EN_PASSANT      = 1 << EN_PASSANT_INDEX;
    int KING_CASTLE     = 1 << KING_CASTLE_INDEX;
    int QUEEN_CASTLE    = 1 << QUEEN_CASTLE_INDEX;
    int QUIET_MOVE      = 0b1 << QUIET_MOVE_INDEX;
    int PROMOTION       = 0b111 << PROMOTION_INDEX;

    static int getPieceCaptured(int moveInfo) {
        return moveInfo & PIECE_CAPTURED;
    }

    static boolean isEnPassant(int moveInfo) {
        return (moveInfo & EN_PASSANT) != 0;
    }

    static boolean isKingCastle(int moveInfo) {
        return (moveInfo & KING_CASTLE) != 0;
    }

    static boolean isQueenCastle(int moveInfo) {
        return (moveInfo & QUEEN_CASTLE) != 0;
    }

    static boolean isQuietMove(int moveInfo) {
        return (moveInfo & QUIET_MOVE) != 0;
    }


    static int getPromotionPiece(int moveInfo) {
        return (moveInfo & PROMOTION) >> PROMOTION_INDEX;
    }
}
