package engine.internal;

import game.ChessUtils;
import game.Move;

import java.util.Arrays;
import java.util.HashMap;

public class BitBoard {

    private static long generateFile(int file) {
        long start = 1;
        for (int i = 0; i < 7; i++) {
            start <<= 8;
            start += 1;
        }
        return start << (7 - file);
    }

    public static final long FILE_A = generateFile(0);
    public static final long FILE_B = generateFile(1);
    public static final long FILE_C = generateFile(2);
    public static final long FILE_D = generateFile(3);
    public static final long FILE_E = generateFile(4);
    public static final long FILE_F = generateFile(5);
    public static final long FILE_G = generateFile(6);
    public static final long FILE_H = generateFile(7);

    public static final long[] FILES = {FILE_A, FILE_B, FILE_C, FILE_D, FILE_E, FILE_F, FILE_G, FILE_H};


    public static final long RANK_1 = 0b11111111L;
    public static final long RANK_2 = 0b11111111L << 8;
    public static final long RANK_3 = 0b11111111L << 16;
    public static final long RANK_4 = 0b11111111L << 24;
    public static final long RANK_5 = 0b11111111L << 32;
    public static final long RANK_6 = 0b11111111L << 40;
    public static final long RANK_7 = 0b11111111L << 48;
    public static final long RANK_8 = 0b11111111L << 56;

    public static final long[] RANKS = {RANK_1, RANK_2, RANK_3, RANK_4, RANK_5, RANK_6, RANK_7, RANK_8};

    public static final int PAWN    = 0;
    public static final int BISHOP  = 1;
    public static final int KNIGHT  = 2;
    public static final int ROOK    = 3;
    public static final int QUEEN   = 4;
    public static final int KING    = 5;

    public static final int WHITE   = 0;
    public static final int BLACK   = 6;

    // Board structure
    // Single array of long of size 16
    // long[] board = new long[16];
    // Where index:
    public static final int WHITE_PAWN      = WHITE + PAWN;
    public static final int WHITE_BISHOP    = WHITE + BISHOP;
    public static final int WHITE_KNIGHT    = WHITE + KNIGHT;
    public static final int WHITE_ROOK      = WHITE + ROOK;
    public static final int WHITE_QUEEN     = WHITE + QUEEN;
    public static final int WHITE_KING      = WHITE + KING;
    public static final int BLACK_PAWN      = BLACK + PAWN;
    public static final int BLACK_BISHOP    = BLACK + BISHOP;
    public static final int BLACK_KNIGHT    = BLACK + KNIGHT;
    public static final int BLACK_ROOK      = BLACK + ROOK;
    public static final int BLACK_QUEEN     = BLACK + QUEEN;
    public static final int BLACK_KING      = BLACK + KING;
    public static final int WHITE_PIECES    = 12;
    public static final int BLACK_PIECES    = 13;
    public static final int ALL_PIECES      = 14;
    public static final int GAME_INFO       = 15;


    public static final char[] INDEX_TO_PIECES = {
            'P',
            'B',
            'N',
            'R',
            'Q',
            'K',
            'p',
            'b',
            'n',
            'r',
            'q',
            'k',
    };

    public static final HashMap<Character, Integer> PIECES_TO_INDEX = new HashMap<>(INDEX_TO_PIECES.length);

    static {
        for (int i = 0; i < INDEX_TO_PIECES.length; i++) {
            PIECES_TO_INDEX.put(INDEX_TO_PIECES[i], i);
        }

    }

    public static String toString(long[] board) {
        StringBuilder builder = new StringBuilder();
        char[] pseudoBoard = new char[64];
        Arrays.fill(pseudoBoard, ' ');

        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 64; j++) {
                if ((board[i] & 1L << j) != 0) {
                    pseudoBoard[63 - j] = INDEX_TO_PIECES[i];
                }
            }
        }

        builder.append("    A   B   C   D   E   F   G   H\n");
        builder.append("  +---+---+---+---+---+---+---+---+\n");
        for (int i = 0; i < 8; i++) {
            builder.append(8 - i);
            builder.append(" | ");
            for (int j = 0; j < 8; j++) {
                builder.append(pseudoBoard[i * 8 + j]);
                builder.append(" | ");
            }
            builder.append(8 - i);
            builder.append("\n  +---+---+---+---+---+---+---+---+\n");
        }
        builder.append("    A   B   C   D   E   F   G   H  \n");
        builder.append("Board FEN: ");
        builder.append(toFEN(board));

        return builder.toString();
    }


    public static int playMove(long[] board, int move) {
        // Move info
        int start = BitMove.fromIndex(move);
        int end = BitMove.toIndex(move);
        int pieceType = BitMove.pieceMoved(move);
        int enPassant = BitMove.enPassantTarget(move);

        boolean isWhiteTurn = GameInfo.isWhiteTurn(board[GAME_INFO]);
        int startIndex = isWhiteTurn ? WHITE : BLACK;
        int opposingIndex = BLACK - startIndex;
        int moveInfo = MoveInfo.QUIET_MOVE;

        long endMask = 1L << end;
        long notEndMask = ~endMask;

        // Clearing starting square
        board[startIndex+pieceType] &= ~(1L << start);

        // Getting piece eaten
        for (int i = 0; i < 12; i++) {
            if ((board[i] & endMask) != 0) {
                moveInfo = i % 6;
                board[i] &= notEndMask; // Clearing the target square
                break;
            }
        }

        // Setting target square
        board[startIndex+pieceType] |= 1L << end;

        // Special piece treatment
        switch (pieceType) {
            case PAWN -> {
                // Checking for promotion
                if ((endMask & (RANK_1 |RANK_8)) != 0) {
                    int promotionPiece = BitMove.promotionPiece(move);
                    // Clearing target square since pawn doesn't exist anymore
                    board[startIndex+PAWN] &= notEndMask;

                    // Adding promotion type
                    board[startIndex+promotionPiece] |= endMask;
                    moveInfo |= promotionPiece << MoveInfo.PROMOTION_INDEX;
                }

                // Checking for en passant
                if (GameInfo.enPassantTargetSquare(board[GAME_INFO]) == end && end != 0) { // Avoid checking en passant at wrong index
                    // Clearing the pawn behind it
                    board[opposingIndex+PAWN] &= ~(1L << (end + (isWhiteTurn ? -8 : 8)));
                    moveInfo = PAWN | MoveInfo.EN_PASSANT;
                }

            }
            case ROOK -> {
                // If it is the rook that moved, then castling is no longer available
                // White side
                if (isWhiteTurn) {
                    if (((FILE_A & RANK_1) & board[WHITE_ROOK]) == 0) {
                        // West side has moved
                        board[GAME_INFO] = GameInfo.setCanWhiteCastleQueenSide(board[GAME_INFO], 0);
                    }
                    if (((FILE_H & RANK_1) & board[WHITE_ROOK]) == 0){
                        // East side has moved
                        board[GAME_INFO] = GameInfo.setCanWhiteCastleKingSide(board[GAME_INFO], 0);
                    }
                } else { // Black side
                    if (((FILE_A & RANK_8) & board[BLACK_ROOK]) == 0) {
                        // West side has moved
                        board[GAME_INFO] = GameInfo.setCanBlackCastleQueenSide(board[GAME_INFO], 0);
                    }
                    if (((FILE_H & RANK_8) & board[BLACK_ROOK]) == 0){
                        // East side has moved
                        board[GAME_INFO] = GameInfo.setCanBlackCastleKingSide(board[GAME_INFO], 0);
                    }
                }
            }
            case KING -> {
                // Checking for castling
//                System.out.println("start-end: " + (start-end));
                if (end-start == 2) {
                    // West side castling
                    long rank = isWhiteTurn ? RANK_1 : RANK_8;
                    board[startIndex + ROOK] &= ~(FILE_A & rank);
                    board[startIndex + ROOK] |= (1L << (end-1));
                    moveInfo |= MoveInfo.QUEEN_CASTLE;
                } else if (start-end == 2) {
                    // East side castling
                    long rank = isWhiteTurn ? RANK_1 : RANK_8;
                    board[startIndex + ROOK] &= ~(FILE_H & rank);
                    board[startIndex + ROOK] |= (1L << (end+1));
                    moveInfo |= MoveInfo.KING_CASTLE;
                }
                if (isWhiteTurn) {
                    board[GAME_INFO] = GameInfo.setCanWhiteCastleKingSide(board[GAME_INFO], 0);
                    board[GAME_INFO] = GameInfo.setCanWhiteCastleQueenSide(board[GAME_INFO], 0);
                } else {
                    board[GAME_INFO] = GameInfo.setCanBlackCastleKingSide(board[GAME_INFO], 0);
                    board[GAME_INFO] = GameInfo.setCanBlackCastleQueenSide(board[GAME_INFO], 0);
                }
            }
        }

        // If the rook is eaten, then you can't castle
        if ((endMask & (FILE_A & RANK_1)) != 0) {
            board[GAME_INFO] = GameInfo.setCanWhiteCastleQueenSide(board[GAME_INFO], 0);
        } else if ((endMask & (FILE_H & RANK_1)) != 0) {
            board[GAME_INFO] = GameInfo.setCanWhiteCastleKingSide(board[GAME_INFO], 0);
        } else if ((endMask & (FILE_A & RANK_8)) != 0) {
            board[GAME_INFO] = GameInfo.setCanBlackCastleQueenSide(board[GAME_INFO], 0);
        } else if ((endMask & (FILE_H & RANK_8)) != 0) {
            board[GAME_INFO] = GameInfo.setCanBlackCastleKingSide(board[GAME_INFO], 0);
        }


        // Clearing en passant whether the piece was eaten or not
        board[GAME_INFO] = GameInfo.setEnPassantTargetSquare(board[GAME_INFO], enPassant);


        board[GAME_INFO] ^= 1; // Swapping turn

        // TODO: Set the rest of the info

        refreshPieces(board);

        return moveInfo;
    }

    public static int playMove(long[] board, String move) {
        // Gotta know which piece makes the move
        int move_ = BitMove.algebraicToMove(move);
        int start = BitMove.fromIndex(move_);
        int end = BitMove.toIndex(move_);
        int promotion = BitMove.promotionPiece(move_);
        int piece = 0;
        for (int i = 0; i < 12; i++) {
            if ((board[i] & (1L << start)) != 0) {
                piece = i % 6; // Whether white or black
                break;
            }
        }
        return playMove(board, BitMove.from(start, end, piece, promotion));
    }

    static void refreshPieces(long[] board) {
        board[WHITE_PIECES] = board[WHITE_PAWN] | board[WHITE_BISHOP] | board[WHITE_KNIGHT] | board[WHITE_ROOK] | board[WHITE_QUEEN] | board[WHITE_KING];
        board[BLACK_PIECES] = board[BLACK_PAWN] | board[BLACK_BISHOP] | board[BLACK_KNIGHT] | board[BLACK_ROOK] | board[BLACK_QUEEN] | board[BLACK_KING];
        board[ALL_PIECES] = board[WHITE_PIECES] | board[BLACK_PIECES];
    }

    public static long[] fromFEN(String FEN) {
        long[] board = new long[16];
        // Assuming FEN passed is valid
        String[] parts = FEN.split(" ");

        // Parsing pieces
        int currentIndex = 0;
        char[] pieces = parts[0].replace("/", "").toCharArray();
        for (char c : pieces) {
            if (Character.isAlphabetic(c)) {
                board[PIECES_TO_INDEX.get(c)] |= 1L << (63 - currentIndex);
                currentIndex += 1;
            } else {
                // Digit
                currentIndex += c - '0';
            }
        }

        // Turn to play
        boolean isWhiteTurn = parts[1].equals("w");

        boolean canWhiteCastleKingSide = parts[2].contains("K");
        boolean canWhiteCastleQueenSide = parts[2].contains("Q");
        boolean canBlackCastleKingSide = parts[2].contains("k");
        boolean canBlackCastleQueenSide = parts[2].contains("q");

        int enPassantTargetSquare = 0;
        if (!parts[3].equals("-")) {
            enPassantTargetSquare = 63 - ChessUtils.algebraicToIndex(parts[3]);
        }

        int halfMoveClock = Integer.parseInt(parts[4]);
        int fullMoveClock = Integer.parseInt(parts[5]);


        board[GAME_INFO] = GameInfo.from(
                isWhiteTurn,
                canWhiteCastleKingSide,
                canWhiteCastleQueenSide,
                canBlackCastleKingSide,
                canBlackCastleQueenSide,
                enPassantTargetSquare,
                halfMoveClock,
                fullMoveClock);


        refreshPieces(board);

        return board;
    }

    public static String toFEN(long[] board) {
        StringBuilder builder = new StringBuilder(90); // Around max num of chars

        // Current Pieces
        int currentSpaces = 0;
        for (int i = 63; i >= 0; i--) {
            for (int j = 0; j < 12; j++) {
                // If current piece is in that square
                if ((board[j] & (1L << i)) != 0) {
                    if (currentSpaces != 0) {
                        builder.append(currentSpaces);
                        currentSpaces = 0;
                    }
                    // Piece is present
                    builder.append(INDEX_TO_PIECES[j]);
                    currentSpaces -= 1;
                    break;
                }
            }
            currentSpaces += 1;
            // If end of line
            if (i % 8 == 0) {
                // Adding empty spaces
                if (currentSpaces != 0) {
                    builder.append(currentSpaces);
                    currentSpaces = 0;
                }
                builder.append('/');
            }
        }
        builder.deleteCharAt(builder.length() - 1); // Removing last /
        builder.append(' ');

        // Current turn
        long info = board[GAME_INFO];
        builder.append(GameInfo.isWhiteTurn(info) ? 'w' : 'b');
        builder.append(' ');

        // Castle sides
        int initialLength = builder.length();
        if (GameInfo.canWhiteCastleKingSide(info)) builder.append('K');
        if (GameInfo.canWhiteCastleQueenSide(info)) builder.append('Q');
        if (GameInfo.canBlackCastleKingSide(info)) builder.append('k');
        if (GameInfo.canBlackCastleQueenSide(info)) builder.append('q');
        if (initialLength == builder.length()) {
            // Nothing changed
            builder.append('-');
        }
        builder.append(' ');

        // En passant target square
        long epts = GameInfo.enPassantTargetSquare(info);
        if (epts == 0) {
            builder.append('-');
        } else {
            builder.append(ChessUtils.indexToAlgebraic(63 - GameInfo.enPassantTargetSquare(info)));
        }
        builder.append(' ');

        // Half-move clock
        builder.append(GameInfo.halfMoveClock(info));
        builder.append(' ');

        // Full-move clock
        builder.append(GameInfo.fullMoveNumber(info));

        return builder.toString();
    }

}
