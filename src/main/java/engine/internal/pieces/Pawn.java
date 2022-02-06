package engine.internal.pieces;

import static engine.internal.BitBoard.*;

public interface Pawn {

    // =============== Pushes =============== //
    static long whiteSinglePush(final long whitePawns) {
        return whitePawns << 8;
    }

    static long whiteDoublePush(final long whitePawns) {
        // Only rank 2 are able to double push
        return (whitePawns & RANK_2) << 16;
    }

    static long blackSinglePush(final long blackPawns) {
        return blackPawns >>> 8;
    }

    static long blackDoublePush(final long blackPawns) {
        // Only rank 7 are able to double push
        return (blackPawns & RANK_7) >>> 16;
    }
    // =============== Pushes =============== //

    // =============== Attacks =============== //
    static long whiteEastAttack(final long whitePawns) {
        return (whitePawns << 7) & ~FILE_A;
    }

    static long whiteWestAttack(final long whitePawns) {
        return (whitePawns << 9) & ~FILE_H;
    }

    static long whiteAttack(final long whitePawns) {
        return whiteEastAttack(whitePawns) | whiteWestAttack(whitePawns);
    }

    static long blackEastAttack(final long blackPawns) {
        return (blackPawns >>> 9) &  ~FILE_A;
    }

    static long blackWestAttack(final long blackPawns) {
        return (blackPawns >>> 7) & ~FILE_H;
    }

    static long blackAttack(final long blackPawns) {
        return blackEastAttack(blackPawns) | blackWestAttack(blackPawns);
    }
    // =============== Attacks =============== //

}
