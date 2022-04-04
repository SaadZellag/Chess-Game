package engine.internal;

import game.Move;
import game.Piece;
import game.PieceType;

import java.util.ArrayList;
import java.util.List;

public interface ChessPassthrough {

    static List<Move> generatePossibleMoves(String FEN) {
        // Converting current board to bitboard
        long[] bitboard = BitBoard.fromFEN(FEN);
        boolean isWhite = GameInfo.isWhiteTurn(bitboard[BitBoard.GAME_INFO]);

        List<Integer> moves = MoveGen.generateLegalMoves(bitboard);
        List<Move> convertedMoves = new ArrayList<>(moves.size());

        for (int move : moves) {
            long[] bitboardClone = bitboard.clone();
            int start = 63 - BitMove.fromIndex(move);
            int end = 63 - BitMove.toIndex(move);
            int pieceMoved = BitMove.pieceMoved(move);
            int promotion = BitMove.promotionPiece(move);

            // Playing the move on the board to get info back
            // Avoids having to do all the logic in playing the moves
            int info = BitBoard.playMove(bitboardClone, move);

            Piece piece = new Piece(isWhite, PieceType.VALUES[pieceMoved]);

            if (promotion != 0) {
                convertedMoves.add(new Move(piece, start, end, PieceType.VALUES[promotion], bitboardClone[BitBoard.GAME_INFO]));
                continue;
            }

            Move.Info moveInfo = null;

            if (MoveInfo.isEnPassant(info)) {
                moveInfo = Move.Info.EN_PASSANT;
            } else if (MoveInfo.isKingCastle(info)) {
                moveInfo = Move.Info.KING_CASTLE;
            } else if (MoveInfo.isQueenCastle(info)) {
                moveInfo = Move.Info.QUEEN_CASTLE;
            }

            if (moveInfo == null) {
                // Normal move
                convertedMoves.add(new Move(piece, start, end, bitboardClone[BitBoard.GAME_INFO]));
            } else {
                convertedMoves.add(new Move(piece, start, end, moveInfo, bitboardClone[BitBoard.GAME_INFO]));
            }
        }

        return convertedMoves;
    }
}
