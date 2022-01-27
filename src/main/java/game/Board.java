package game;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public class Board {
    private Piece[] pieces = new Piece[64];

    private boolean isWhiteTurn;

    private boolean canWhiteCastleKingSide;
    private boolean canWhiteCastleQueenSide;
    private boolean canBlackCastleKingSide;
    private boolean canBlackCastleQueenSide;

    private int enPassantTargetSquare;

    private int halfMoveClock;

    private int fullMoveNumber;

    private void parsePieces(String placements) {
        // Removing useless /
        placements = placements.replace("/", "");
        int currentIndex = 0;
        for (char currentChar : placements.toCharArray()) {
            // If it is a number
            if (Character.isDigit(currentChar)) {
                currentIndex += (int) (currentChar) - 48; // According to ASCII
                continue;
            }

            PieceType type = ChessUtils.charToPieceType(currentChar);

            this.pieces[currentIndex] = new Piece(Character.isUpperCase(currentChar), type);
            currentIndex++;
        }
    }

    private void parseCurrentTurn(String turn) {
        this.isWhiteTurn = switch (turn) {
            case "w" -> true;
            case "b" -> false;
            default -> throw new IllegalStateException("Unexpected value: " + turn);
        };
    }

    private void parseCastling(String castling) {
        for (char currentChar : castling.toCharArray()) {
            switch (currentChar) {
                case 'K' -> this.canWhiteCastleKingSide = true;
                case 'Q' -> this.canWhiteCastleQueenSide = true;
                case 'k' -> this.canBlackCastleKingSide = true;
                case 'q' -> this.canBlackCastleQueenSide = true;
                case '-' -> {
                    return;
                }
                default -> throw new IllegalArgumentException("Invalid FEN Notation");
            }
        }
    }

    private void parseEnPassantSquare(String enPassant) throws IllegalArgumentException {
        if (enPassant.equals("-")) {
            this.enPassantTargetSquare = -1;
        } else {
            this.enPassantTargetSquare = ChessUtils.algebraicToIndex(enPassant);
        }
    }

    public Board(String FEN) throws IllegalArgumentException {
        // Parsing the FEN following https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation
        String[] parts = FEN.split(" ");

        if (parts.length != 6) {
            throw new IllegalArgumentException("Incorrect FEN Size: " + FEN);
        }

        // Pieces
        parsePieces(parts[0]);

        // Active Color
        parseCurrentTurn(parts[1]);

        // Who can castle
        parseCastling(parts[2]);

        // En passant
        parseEnPassantSquare(parts[3]);

        // Half Move Clock
        this.halfMoveClock = Integer.parseUnsignedInt(parts[4]);

        // Full Move Clock
        this.fullMoveNumber = Integer.parseUnsignedInt(parts[5]);
    }

    public String toFEN() {
        StringBuilder FENString = new StringBuilder();

        // Current board
        for (int i = 0; i < 8; i++) {
            int currentSpaces = 0;
            for (int j = 0; j < 8; j++) {
                Piece currentPiece = this.pieces[i*8+j];

                // Handling empty spaces
                if (currentPiece == null) {
                    currentSpaces += 1;
                    continue;
                } else if (currentSpaces > 0) {
                    FENString.append(currentSpaces);
                    currentSpaces = 0;
                }

                char repr = ChessUtils.pieceTypeToChar(currentPiece.type, currentPiece.isWhite);

                FENString.append(repr);
            }
            if (currentSpaces > 0) {
                FENString.append(currentSpaces);
            }
            FENString.append('/');
        }
        FENString.deleteCharAt(FENString.length()-1); // Removing last /
        FENString.append(' ');

        // Current turn
        FENString.append(this.isWhiteTurn ? 'w' : 'b');
        FENString.append(' ');

        // Castling
        int initialLength = FENString.length();
        if (this.canWhiteCastleKingSide) FENString.append('K');
        if (this.canWhiteCastleQueenSide) FENString.append('Q');
        if (this.canBlackCastleKingSide) FENString.append('k');
        if (this.canBlackCastleQueenSide) FENString.append('q');
        if (initialLength == FENString.length()) {
            // Nothing changed
            FENString.append('-');
        }
        FENString.append(' ');

        // En passant target square
        if (this.enPassantTargetSquare >= 0) {
            FENString.append(ChessUtils.indexToAlgebraic(this.enPassantTargetSquare));
        } else {
            FENString.append('-');
        }
        FENString.append(' ');

        // Half-move clock
        FENString.append(this.halfMoveClock);
        FENString.append(' ');

        // Full-move clock
        FENString.append(this.fullMoveNumber);

        return FENString.toString();
    }

    // Returns an optional containing if the piece was eaten
    public Optional<Piece> playMove(Move move) {
        Objects.requireNonNull(move);

        // Making sure the move makes sense
        // DOES NOT CHECK IF MOVE IS LEGAL

        // The original place is the same as in board
        Piece initialPiece = this.pieces[move.initialLocation];
        if (initialPiece == null || !initialPiece.equals(move.piece)) {
            throw new IllegalArgumentException("Initial position does not match the current board");
        }

        Optional<Piece> pieceEaten;
        Piece finalPiece = this.pieces[move.finalLocation];
        // The destination piece must not be the same type (white cannot eat white)
        if (finalPiece != null) {
            if (finalPiece.isWhite == move.piece.isWhite) {
                throw new IllegalArgumentException("Cannot eat piece of the same color");
            }
            this.pieces[move.finalLocation] = initialPiece;
            pieceEaten = Optional.of(finalPiece);
        } else {
            pieceEaten = Optional.empty();
        }

        return pieceEaten;
    }

    public Piece[] getPieces() {
        return pieces.clone(); // Avoid modifying from the outside
    }

    public boolean isWhiteTurn() {
        return isWhiteTurn;
    }

    public boolean canWhiteCastleKingSide() {
        return canWhiteCastleKingSide;
    }

    public boolean canWhiteCastleQueenSide() {
        return canWhiteCastleQueenSide;
    }

    public boolean canBlackCastleKingSide() {
        return canBlackCastleKingSide;
    }

    public boolean canBlackCastleQueenSide() {
        return canBlackCastleQueenSide;
    }

    public int getEnPassantTargetSquare() {
        return enPassantTargetSquare;
    }

    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public int getFullMoveNumber() {
        return fullMoveNumber;
    }
}
