package engine.internal;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class BitUtilsTest {

    @Test
    void testPassthrough() {
        Random r = new Random(123);

        // Testing 100 000 random boards
        for (int i = 0; i < 100_000; i++) {
            long generated = r.nextLong();
            long passed = BitUtils.stringToBoard(BitUtils.boardToString(generated));
            assertEquals(Long.toBinaryString(generated), Long.toBinaryString(passed));
        }
    }

}