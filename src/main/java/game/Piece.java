package game;

import java.util.Objects;

public class Piece {
    public final boolean isWhite;
    public final PieceType type;

    public Piece(boolean isWhite, PieceType type) {
        this.isWhite = isWhite;
        this.type = Objects.requireNonNull(type);
    }


    @Override
    public String toString() {
        return (isWhite ? "White" : "Black") + " " + type;
    }

    @Override
    public boolean equals(Object o) {
        // Generated by IDE
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Piece piece = (Piece) o;

        if (isWhite != piece.isWhite) return false;
        return type == piece.type;
    }

    @Override
    public int hashCode() {
        int result = (isWhite ? 1 : 0);
        result = 31 * result + type.hashCode();
        return result;
    }
}