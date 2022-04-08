package engine.internal;

import engine.internal.pieces.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static engine.internal.BitBoard.*;

public class MoveGen {

    private MoveGen() {
    }


    private static void addPromotion(final int from, final int to, final List<Integer> moves) {
        moves.add(BitMove.fromPromotion(from, to , QUEEN));
        moves.add(BitMove.fromPromotion(from, to, ROOK));
        moves.add(BitMove.fromPromotion(from, to, BISHOP));
        moves.add(BitMove.fromPromotion(from, to, KNIGHT));
    }

    private static void generatePawnMoves(boolean isWhite, long currentPawns, long opposingPieces, long allPieces, int enPassantTargetSquare, List<Integer> moves) {

        int singlePushShift = isWhite ? -8 : 8;
        int doublePushShift = isWhite ? -16 : 16;
        int westShift = isWhite ? -9 : 7;
        int eastShift = isWhite ? -7 : 9;

        long enPassantMask = (1L << enPassantTargetSquare) & (RANK_3 | RANK_6);
        final long promotionMask = RANK_8 | RANK_1;

        // Pushes
        long singlePushes;
        long doublePushes;

        // Captures
        long pawnWest;
        long pawnEast;

        if (isWhite) {
            singlePushes = Pawn.whiteSinglePush(currentPawns) & ~allPieces; // Prevent pawns pushing into other pieces
            doublePushes = Pawn.whiteDoublePush(currentPawns) & singlePushes << 8 & ~allPieces; // If can't single push, then can't double push

            pawnWest = Pawn.whiteWestAttack(currentPawns) & (opposingPieces | enPassantMask);
            pawnEast = Pawn.whiteEastAttack(currentPawns) & (opposingPieces | enPassantMask);
        } else {
            singlePushes = Pawn.blackSinglePush(currentPawns) & ~allPieces; // Prevent pawns pushing into other pieces
            doublePushes = Pawn.blackDoublePush(currentPawns) & singlePushes >>> 8 & ~allPieces; // If can't single push, then can't double push

            pawnWest = Pawn.blackWestAttack(currentPawns) & (opposingPieces | enPassantMask);
            pawnEast = Pawn.blackEastAttack(currentPawns) & (opposingPieces | enPassantMask);
        }

        // Adding promotions
        int j;
        while ((j = Long.numberOfTrailingZeros(singlePushes & promotionMask)) != 64) {
            addPromotion(j+singlePushShift, j, moves);
            singlePushes &= ~(1L << j);
        }

        while ((j = Long.numberOfTrailingZeros(pawnWest & promotionMask)) != 64) {
            addPromotion(j+westShift, j, moves);
            pawnWest &= ~(1L << j);
        }

        while ((j = Long.numberOfTrailingZeros(pawnEast & promotionMask)) != 64) {
            addPromotion(j+eastShift, j, moves);
            pawnEast &= ~(1L << j);
        }

        // Adding pushes
        // Single pushes
        while ((j = Long.numberOfTrailingZeros(singlePushes)) != 64) {
            moves.add(BitMove.from(j+singlePushShift, j, PAWN));
            singlePushes &= singlePushes-1;
        }
        // Double Pushes
        while ((j = Long.numberOfTrailingZeros(doublePushes)) != 64) {
            moves.add(BitMove.fromEnPassant(j+doublePushShift, j, j+singlePushShift));
            doublePushes &= doublePushes-1;
        }

        // Adding Attacks
        while ((j = Long.numberOfTrailingZeros(pawnWest)) != 64) {
            moves.add(BitMove.from(j+westShift, j, PAWN));
            pawnWest &= pawnWest-1;
        }
        while ((j = Long.numberOfTrailingZeros(pawnEast)) != 64) {
            moves.add(BitMove.from(j+eastShift, j, PAWN));
            pawnEast &= pawnEast-1;
        }
    }

    private static void generateSlidingPieces(long pieces, long currentPieces, long allPieces, int pieceType, List<Integer> moves, BiFunction<Integer, Long, Long> attacksGetter) {
        int i, j;
        long attacks;
        // Getting every piece index
        while ((i = Long.numberOfTrailingZeros(pieces)) != 64) {
            attacks = attacksGetter.apply(i, allPieces) & ~(currentPieces); // Removing eating own pieces

            // Getting every attack index
            while ((j = Long.numberOfTrailingZeros(attacks)) != 64) {
                moves.add(BitMove.from(i, j, pieceType));
                attacks &= attacks-1;
            }
            pieces &= pieces-1;
        }
    }

    private static void generateBishopMoves(long bishops, long currentPieces, long allPieces, List<Integer> moves) {
        generateSlidingPieces(bishops, currentPieces, allPieces, BISHOP, moves, Bishop::getAttack);
    }

    private static void generateKnightMoves(long knights, long currentPieces, List<Integer> moves) {
        int i, j;
        long attacks;

        while ((i = Long.numberOfTrailingZeros(knights)) != 64) {
            attacks = Knight.ATTACKS[i] & ~(currentPieces); // Removing eating own pieces

            // Getting every attack index
            while ((j = Long.numberOfTrailingZeros(attacks)) != 64) {
                moves.add(BitMove.from(i, j, KNIGHT));
                attacks &= attacks-1;
            }
            knights &= knights-1;
        }
    }

    private static void generateRookMoves(long rooks, long currentPieces, long allPieces, List<Integer> moves) {
        generateSlidingPieces(rooks, currentPieces, allPieces, ROOK, moves, Rook::getAttack);
    }

    private static void generateQueenMoves(long queen, long currentPieces, long allPieces, List<Integer> moves) {
        generateSlidingPieces(queen, currentPieces, allPieces, QUEEN, moves, Queen::getAttack);
    }

    private static void generateKingMoves(long currentKing, long currentPieces, long attackers, long allPieces, List<Integer> moves, boolean canCastleWest, boolean canCastleEast) {
        int i = Long.numberOfTrailingZeros(currentKing), j;
        long attacks = King.getAttack(currentKing) & ~currentPieces; // Preventing eating current pieces
        while ((j = Long.numberOfTrailingZeros(attacks)) != 64) {
            moves.add(BitMove.from(i, j, KING));
            attacks &= attacks-1;
        }

        // Castling
        if (canCastleWest) {
            long kingPath = (currentKing << 2) | (currentKing << 1) | currentKing;
            long rookPath = (currentKing << 3) | (currentKing << 2) | (currentKing << 1); // Moving to the left
            if (((kingPath & attackers) == 0) && (rookPath & allPieces) == 0) { // No attacking piece or ally pieces
                moves.add(BitMove.from(i, i+2, KING));
            }
        }
        if (canCastleEast) {
            long kingPath = (currentKing >>> 2) | (currentKing >>> 1) | currentKing;
            long rookPath = (currentKing >>> 2) | (currentKing >>> 1); // Moving to the right
            if (((kingPath & attackers) == 0) && (rookPath & allPieces) == 0) { // No attacking piece or ally pieces
                moves.add(BitMove.from(i, i-2, KING));
            }
        }
    }

    private static List<Integer> generatePseudoLegalMoves(long[] board) {
        List<Integer> moves = new ArrayList<>(64);
        boolean isWhiteTurn = (board[GAME_INFO] & 1) == 1;
        int startIndex = isWhiteTurn ? WHITE : BLACK;


        long currentPieces      = board[isWhiteTurn ? WHITE_PIECES : BLACK_PIECES];
        long opposingPieces     = board[isWhiteTurn ? BLACK_PIECES : WHITE_PIECES];
        long allPieces          = board[ALL_PIECES];
        long gameInfo           = board[GAME_INFO];

        boolean canCastleWest   = isWhiteTurn ? GameInfo.canWhiteCastleQueenSide(gameInfo) : GameInfo.canBlackCastleQueenSide(gameInfo);
        boolean canCastleEast   = isWhiteTurn ? GameInfo.canWhiteCastleKingSide(gameInfo) : GameInfo.canBlackCastleKingSide(gameInfo);


        // Pawns
        generatePawnMoves(isWhiteTurn, board[startIndex + PAWN], opposingPieces, allPieces, GameInfo.enPassantTargetSquare(gameInfo), moves);

        // Bishops
        generateBishopMoves(board[startIndex + BISHOP], currentPieces, allPieces, moves);

        // Knights
        generateKnightMoves(board[startIndex + KNIGHT], currentPieces, moves);

        // Rooks
        generateRookMoves(board[startIndex + ROOK], currentPieces, allPieces, moves);

        // Queen
        generateQueenMoves(board[startIndex + QUEEN], currentPieces, allPieces, moves);

        // King
        generateKingMoves(board[startIndex + KING], currentPieces, getAttackingSquares(board), allPieces, moves, canCastleWest, canCastleEast);


        return moves;
    }

    private static long getAttackingSquares(long[] board) {
        boolean isWhiteTurn = (board[GAME_INFO] & 1) == 1;
        int startIndex = isWhiteTurn ? BLACK : WHITE;

        long allPieces          = board[ALL_PIECES];

        long attackers = 0;
        // Pawn attacks
        if (isWhiteTurn) {
            attackers |= Pawn.blackAttack(board[startIndex + PAWN]);
        } else {
            attackers |= Pawn.whiteAttack(board[startIndex + PAWN]);
        }

        // Bishop attacks
        attackers |= Bishop.getAttacks(board[startIndex + BISHOP], allPieces);

        // Knight attacks
        attackers |= Knight.getAttacks(board[startIndex + KNIGHT]);

        // Rook attacks
        attackers |= Rook.getAttacks(board[startIndex + ROOK], allPieces);

        // Queen attacks
        attackers |= Queen.getAttacks(board[startIndex + QUEEN], allPieces);

        // King attacks :/
        attackers |= King.getAttack(board[startIndex + KING]);

        return attackers;
    }

    public static boolean isInCheck(long[] board) {
        int startIndex = BLACK - (int) ((board[GAME_INFO] & 1) * 6);
        long currentKing = board[startIndex + KING];

        return (getAttackingSquares(board) & currentKing) != 0;
    }

    private static boolean isMoveLegal(long[] board, int move) {
        BitBoard.playMove(board, move);
        board[GAME_INFO] ^= 1; // Setting turn to opponent
        return isInCheck(board);
    }

    public static List<Integer> generateLegalMoves(long[] board) {
        List<Integer> pseudoLegalMoves = generatePseudoLegalMoves(board);

        pseudoLegalMoves.removeIf(move -> isMoveLegal(board.clone(), move));

        return pseudoLegalMoves;
    }


}
