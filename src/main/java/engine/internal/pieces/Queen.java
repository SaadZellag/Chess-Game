package engine.internal.pieces;

public interface Queen {

    static long getAttack(int square, long blockers) {
        // Queen is just rook and bishop combined
        return Rook.getAttack(square, blockers) | Bishop.getAttack(square, blockers);
    }
}
