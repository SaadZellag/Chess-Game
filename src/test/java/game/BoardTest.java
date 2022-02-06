package game;

import engine.internal.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    List<String> FENS;

    {
        // Reading a lot of sample fens
        String path = this.getClass().getClassLoader().getResource("100_000_FENS.txt").getFile();
        if (path == null) {
            fail();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            FENS = reader.lines().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFromFEN() {
        // Starting Position
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

        // After move 1.e4
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));

        // After 1...c5:
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"));

        // After 2.Nf3
        assertDoesNotThrow(() -> new Board("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"));

        for (String FEN : FENS) {
            assertDoesNotThrow(() -> new Board(FEN));
        }
    }

    @Test
    void testPassthrough() {
        for (String FEN : FENS) {
            assertEquals(FEN, new Board(FEN).toFEN());
        }
    }

    @Test
    void testMoveGen() {
        for (String FEN : FENS) {
            Board board = new Board(FEN);
            long[] bitboard = BitBoard.fromFEN(FEN);

            List<Move> moves = board.generatePossibleMoves();
            List<Integer> bitMoves = MoveGen.generateLegalMoves(bitboard);

            assertEquals(bitMoves.size(), moves.size());
        }
    }

    @Test
    void testMovePlaying() {
        for (String FEN : FENS) {
            Board board = new Board(FEN);
            long[] bitboard = BitBoard.fromFEN(FEN);

            List<Move> moves = board.generatePossibleMoves();
            List<Integer> bitMoves = MoveGen.generateLegalMoves(bitboard);


            for (int i = 0; i < moves.size(); i++) {
                Board boardCopy = board.clone();
                long[] bitboardCopy = bitboard.clone();


                boardCopy.playMove(moves.get(i));
                BitBoard.playMove(bitboardCopy, bitMoves.get(i));

                assertEquals(BitBoard.toFEN(bitboardCopy), boardCopy.toFEN());
            }
        }
    }

}