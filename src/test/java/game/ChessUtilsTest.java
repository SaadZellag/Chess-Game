package game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessUtilsTest {

    @Test
    void testConversion() {
        for (int i = 0; i < 64; i++) {
            assertEquals(i, ChessUtils.algebraicToIndex(ChessUtils.indexToAlgebraic(i)));
        }
    }

    @Test
    void indexToAlgebraic() {
        for (int i = 0; i < 64; i++) {
            int finalI = i;
            assertDoesNotThrow(() -> ChessUtils.indexToAlgebraic(finalI));
        }

        assertThrows(IllegalArgumentException.class, () -> ChessUtils.indexToAlgebraic(-1));
        assertThrows(IllegalArgumentException.class, () -> ChessUtils.indexToAlgebraic(64));
    }

    @Test
    void algebraicToIndex() {
        assertThrows(IllegalArgumentException.class, () -> ChessUtils.algebraicToIndex(""));
        assertThrows(IllegalArgumentException.class, () -> ChessUtils.algebraicToIndex("a9"));
        assertThrows(IllegalArgumentException.class, () -> ChessUtils.algebraicToIndex("l4"));
        assertThrows(IllegalArgumentException.class, () -> ChessUtils.algebraicToIndex("l-3"));

    }
}