package engine.internal;

public interface GameInfo {

    // Game State indexes (from right to left)
    // Bit 1: isWhiteTurn
    // Bit 2: canWhiteCastleKingSide
    // Bit 3: canWhiteCastleQueenSide
    // Bit 4: canBlackCastleKingSide
    // Bit 5: canBlackCastleQueenSide
    // Bit 6-11: enPassantTargetSquare
    // Bit 12-19: halfMoveClock
    // Bit 20-64: fullMoveNumber

    long IS_WHITE_TURN_INDEX                 = 0;
    long CAN_WHITE_CASTLE_KING_SIDE_INDEX    = 1;
    long CAN_WHITE_CASTLE_QUEEN_SIDE_INDEX   = 2;
    long CAN_BLACK_CASTLE_KING_SIDE_INDEX    = 3;
    long CAN_BLACK_CASTLE_QUEEN_SIDE_INDEX   = 4;
    long EN_PASSANT_TARGET_SQUARE_INDEX      = 5;
    long HALF_MOVE_CLOCK_INDEX               = 11;
    long FULL_MOVE_NUMBER_INDEX              = 19;

    long IS_WHITE_TURN                   = 0b00000001 << IS_WHITE_TURN_INDEX;
    long CAN_WHITE_CASTLE_KING_SIDE      = 0b00000001 << CAN_WHITE_CASTLE_KING_SIDE_INDEX;
    long CAN_WHITE_CASTLE_QUEEN_SIDE     = 0b00000001 << CAN_WHITE_CASTLE_QUEEN_SIDE_INDEX;
    long CAN_BLACK_CASTLE_KING_SIDE      = 0b00000001 << CAN_BLACK_CASTLE_KING_SIDE_INDEX;
    long CAN_BLACK_CASTLE_QUEEN_SIDE     = 0b00000001 << CAN_BLACK_CASTLE_QUEEN_SIDE_INDEX;
    long EN_PASSANT_TARGET_SQUARE        = 0b00111111 << EN_PASSANT_TARGET_SQUARE_INDEX;
    long HALF_MOVE_CLOCK                 = 0b11111111 << HALF_MOVE_CLOCK_INDEX;
    
    // isWhiteTurn
    // canWhiteCastleKingSide
    // canWhiteCastleQueenSide
    // canBlackCastleKingSide
    // canBlackCastleQueenSide
    // enPassantTargetSquare
    // halfMoveClock
    // fullMoveNumber
    static long from(boolean iwt, boolean cwcks, boolean cwcqs, boolean cbcks, boolean cbcqs, long epts, long hmc, long fmc) {
        return  (iwt ? 1 : 0) | 
                (cwcks ? CAN_WHITE_CASTLE_KING_SIDE : 0) |
                (cwcqs ? CAN_WHITE_CASTLE_QUEEN_SIDE : 0) |
                (cbcks ? CAN_BLACK_CASTLE_KING_SIDE : 0) |
                (cbcqs ? CAN_BLACK_CASTLE_QUEEN_SIDE : 0) |
                (epts << EN_PASSANT_TARGET_SQUARE_INDEX) |
                (hmc << HALF_MOVE_CLOCK_INDEX) |
                (fmc << FULL_MOVE_NUMBER_INDEX);
    }
    
    static boolean isWhiteTurn(long info) {
        return (info & IS_WHITE_TURN) != 0;
    }

    static boolean canWhiteCastleKingSide(long info) {
        return (info & CAN_WHITE_CASTLE_KING_SIDE) != 0;
    }

    static boolean canWhiteCastleQueenSide(long info) {
        return (info & CAN_WHITE_CASTLE_QUEEN_SIDE) != 0;
    }

    static boolean canBlackCastleKingSide(long info) {
        return (info & CAN_BLACK_CASTLE_KING_SIDE) != 0;
    }

    static boolean canBlackCastleQueenSide(long info) {
        return (info & CAN_BLACK_CASTLE_QUEEN_SIDE) != 0;
    }

    static int enPassantTargetSquare(long info) {
        return (int) ((info & EN_PASSANT_TARGET_SQUARE) >>> EN_PASSANT_TARGET_SQUARE_INDEX);
    }
    
    static int halfMoveClock(long info) {
        return (int) ((info & HALF_MOVE_CLOCK) >>> HALF_MOVE_CLOCK_INDEX);
    }

    static int fullMoveNumber(long info) {
        return (int) (info >>> FULL_MOVE_NUMBER_INDEX);
    }

    static long setEnPassantTargetSquare(long info, long epts) {
        return (info & ~EN_PASSANT_TARGET_SQUARE) | (epts << EN_PASSANT_TARGET_SQUARE_INDEX);
    }

    static long setCanWhiteCastleKingSide(long info, long cwcks) {
        return (info & ~CAN_WHITE_CASTLE_KING_SIDE) | (cwcks << CAN_WHITE_CASTLE_KING_SIDE_INDEX);
    }

    static long setCanWhiteCastleQueenSide(long info, long cwcqs) {
        return (info & ~CAN_WHITE_CASTLE_QUEEN_SIDE) | (cwcqs << CAN_WHITE_CASTLE_QUEEN_SIDE_INDEX);
    }

    static long setCanBlackCastleKingSide(long info, long cbcks) {
        return (info & ~CAN_BLACK_CASTLE_KING_SIDE) | (cbcks << CAN_BLACK_CASTLE_KING_SIDE_INDEX);
    }

    static long setCanBlackCastleQueenSide(long info, long cbcqs) {
        return (info & ~CAN_BLACK_CASTLE_QUEEN_SIDE) | (cbcqs << CAN_BLACK_CASTLE_QUEEN_SIDE_INDEX);
    }
}
