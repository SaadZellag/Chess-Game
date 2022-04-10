package game;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class Move implements Serializable {
    public enum Info {
        PROMOTION,
        EN_PASSANT,
        KING_CASTLE,
        QUEEN_CASTLE,
    }

    public final Piece piece;
    public final int initialLocation;
    public final int finalLocation;
    public final PieceType promotionPiece;
    public final Info moveInfo;
    public final long extraInfo; // Contains Castling right

    public Move(Piece piece, int initialLocation, int finalLocation) {
        this(piece, initialLocation, finalLocation, null);
    }

    public Move(Piece piece, int initialLocation, int finalLocation, PieceType promotionPiece) {
       this(piece, initialLocation, finalLocation, promotionPiece, null, 0);
    }

    // Returns the move with extra info in it
    public static Optional<Move> toEngineMove(Board board, Move simpleMove) {
        return board.generatePossibleMoves().stream().filter(m -> m.equals(simpleMove)).findAny();
    }

    // ================================ Should not be used outside the engine =============================== //
    public Move(Piece piece, int initialLocation, int finalLocation, PieceType promotionPiece, Info moveInfo, long extraInfo) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
        this.promotionPiece = promotionPiece;
        this.moveInfo = moveInfo;
        this.extraInfo = extraInfo;
    }
    // ================================ Should not be used outside the engine =============================== //


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Move move = (Move) o;

        if (initialLocation != move.initialLocation) return false;
        if (finalLocation != move.finalLocation) return false;
        if (!piece.equals(move.piece)) return false;
        return promotionPiece == move.promotionPiece;
    }

    @Override
    public int hashCode() {
        int result = piece.hashCode();
        result = 31 * result + initialLocation;
        result = 31 * result + finalLocation;
        result = 31 * result + (promotionPiece != null ? promotionPiece.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return ChessUtils.moveToUCI(this);
    }
}
