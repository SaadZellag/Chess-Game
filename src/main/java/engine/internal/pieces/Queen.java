package engine.internal.pieces;

public interface Queen {

    static long getAttack(final int square, final long blockers) {
        // Queen is just rook and bishop combined
        return Bishop.getAttack(square, blockers) | Rook.getAttack(square, blockers);
    }

     static long getAttacks(long mask, final long blockers) {
        int square;
        long attacks = 0;
        while ((square = Long.numberOfTrailingZeros(mask)) != 64) {
            attacks |= getAttack(square, blockers);
            mask &= mask-1;
        }
        return attacks;
    }

}
