package game;

import java.io.Serializable;
import java.util.Objects;

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
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
        this.promotionPiece = null;
        this.moveInfo = null;
        this.extraInfo = 0;
    }

    public Move(Piece piece, int initialLocation, int finalLocation, long extraInfo) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
        this.promotionPiece = null;
        this.moveInfo = null;
        this.extraInfo = extraInfo;
    }

    public Move(Piece piece, int initialLocation, int finalLocation, PieceType promotionPiece, long extraInfo) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
        this.promotionPiece = Objects.requireNonNull(promotionPiece);
        this.moveInfo = Info.PROMOTION;
        this.extraInfo = extraInfo;
    }

    public Move(Piece piece, int initialLocation, int finalLocation, Info moveInfo, long extraInfo) {
        this.piece = Objects.requireNonNull(piece);
        this.initialLocation = initialLocation;
        this.finalLocation = finalLocation;
        this.promotionPiece = null;
        this.moveInfo = moveInfo;
        this.extraInfo = extraInfo;
    }

}
