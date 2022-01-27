package game;

import java.util.Objects;
import java.util.Optional;

public class Move {
    public final Piece piece;
    public final int initialLocation;
    public final int finalLocation;

    public Move(Piece piece, int initialLocation, int finalLocation) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
    }

}
