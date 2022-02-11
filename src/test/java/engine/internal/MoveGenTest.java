package engine.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Using https://www.chessprogramming.org/Perft_Results
@Execution(ExecutionMode.CONCURRENT)
class MoveGenTest {

    List<String> FENS;

    {
        // Reading a lot of sample fens
        String path = this.getClass().getClassLoader().getResource("PerftTestSample.txt").getFile();
        if (path == null) {
            fail();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            FENS = reader.lines().toList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static long perft(long[] board, int depth) {
        List<Integer> moves = MoveGen.generateLegalMoves(board);
        if (depth == 1) {
            return moves.size();
        }

        long total = 0;
        for (int move : moves) {
            long[] clone = board.clone();
            BitBoard.playMove(clone, move);
            long size = perft(clone, depth-1);


            total += size;
        }
        return total;
    }

    private static long perft(String fen, int depth) {
        return perft(BitBoard.fromFEN(fen), depth);
    }

    @Test
    void perftFast() {
        // First few depths to make the tests fast

        final String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertEquals(20,	    perft(initialFEN, 1));
        assertEquals(400,	    perft(initialFEN, 2));
        assertEquals(8902,	    perft(initialFEN, 3));
        assertEquals(197281,	perft(initialFEN, 4));

        final String position2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
        assertEquals(48,	    perft(position2, 1));
        assertEquals(2039,	    perft(position2, 2));
        assertEquals(97862,	    perft(position2, 3));
        assertEquals(4085603,	perft(position2, 4));

        final String position3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
        assertEquals(14,	    perft(position3, 1));
        assertEquals(191,	    perft(position3, 2));
        assertEquals(2812,	    perft(position3, 3));
        assertEquals(43238,	    perft(position3, 4));

        final String position4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
        assertEquals(6,	        perft(position4, 1));
        assertEquals(264,	    perft(position4, 2));
        assertEquals(9467,	    perft(position4, 3));
        assertEquals(422333,	perft(position4, 4));

        final String position5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
        assertEquals(44,	    perft(position5, 1));
        assertEquals(1486,	    perft(position5, 2));
        assertEquals(62379,	    perft(position5, 3));
        assertEquals(2103487,	perft(position5, 4));

        final String position6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10";
        assertEquals(46,	    perft(position6, 1));
        assertEquals(2079,	    perft(position6, 2));
        assertEquals(89890,	    perft(position6, 3));
        assertEquals(3894594,	perft(position6, 4));
    }

    @Test
    void perftLongInitial() {
        // This test will take a lot of time
        final String initialFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        assertEquals(4865609,       perft(initialFEN, 5));
        assertEquals(119060324,     perft(initialFEN, 6));
        assertEquals(3195901860L,	perft(initialFEN, 7));
    }

    @Test
    void perftLongPosition2() {
        final String position2 = "r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1";
        assertEquals(193690690,	    perft(position2, 5));
        assertEquals(8031647685L,   perft(position2, 6));
    }

    @Test
    void perftLongPosition3() {
        final String position3 = "8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1";
        assertEquals(674624,	    perft(position3, 5));
        assertEquals(11030083,	    perft(position3, 6));
        assertEquals(178633661,	    perft(position3, 7));
        assertEquals(3009794393L,	perft(position3, 8));
    }

    @Test
    void perftLongPosition4() {
        final String position4 = "r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1";
        assertEquals(15833292,	    perft(position4, 5));
        assertEquals(706045033,	    perft(position4, 6));
    }

    @Test
    void perftLongPosition5() {
        final String position5 = "rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8";
        assertEquals(89941194,	    perft(position5, 5));
    }

    @Test
    void perftLongPosition6() {
        final String position6 = "r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10";
        assertEquals(164075551,	    perft(position6, 5));
        assertEquals(6923051137L,	perft(position6, 6));
    }

    @Test
    void perftRandomPositions() {
        for (String FEN : FENS) {
            String[] parts = FEN.split("; ");
            for (int i = 1; i < parts.length; i++) {
                long total = perft(parts[0], i);
                assertEquals(total, Long.parseLong(parts[i].split(" ")[1]));
            }
        }
    }
}