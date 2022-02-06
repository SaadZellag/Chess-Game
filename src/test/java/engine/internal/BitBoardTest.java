package engine.internal;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BitBoardTest {

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
    void testPassthrough() {
        for (String FEN : FENS) {
            assertEquals(FEN, BitBoard.toFEN(BitBoard.fromFEN(FEN)));
        }
    }

}