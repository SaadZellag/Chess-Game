package game;

import java.util.Optional;
import java.util.OptionalInt;

public class Board {
    private Piece[] pieces = new Piece[64];

    private boolean isWhiteTurn;

    private boolean canWhiteCastleKingSide;
    private boolean canWhiteCastleQueenSide;
    private boolean canBlackCastleKingSide;
    private boolean canBlackCastleQueenSide;

    private Integer enPassantTargetSquare;

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

            PieceType type = switch (Character.toLowerCase(currentChar)) {
                case 'k' -> PieceType.KING;
                case 'q' -> PieceType.QUEEN;
                case 'r' -> PieceType.ROOK;
                case 'n' -> PieceType.KNIGHT;
                case 'b' -> PieceType.BISHOP;
                case 'p' -> PieceType.PAWN;
                default -> throw new IllegalStateException("Unexpected value: " + currentChar);
            };

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
            }
        }
    }

    private void parseEnPassantSquare(String enPassant) throws IllegalArgumentException {
        if (enPassant.equals("-")) {
            return;
        }
        this.enPassantTargetSquare = ChessUtils.algebraicToIndex(enPassant);
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
}
