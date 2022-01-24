package game;

import java.util.Objects;
import java.util.Optional;

public class Move {
    private final Piece piece;
    private final int initialLocation;
    private final int finalLocation;

    public Move(Piece piece, int initialLocation, int finalLocation) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getInitialLocation() {
        return initialLocation;
    }

    public int getFinalLocation() {
        return finalLocation;
    }
}
