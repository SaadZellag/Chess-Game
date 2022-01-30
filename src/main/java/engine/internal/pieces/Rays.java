package engine.internal.pieces;

import java.util.Random;
import java.util.function.BiFunction;

public class Rays {

    private Rays () {}

    private static long randomFewBits(Random r) {
        return r.nextLong() & r.nextLong() & r.nextLong();
    }

    static long blockersPermutation(int iteration, long mask) {
        long blockers = 0;

        while (iteration != 0) {
            if ((iteration & 1) != 0) {
                int shift = Long.numberOfTrailingZeros(mask);
                blockers |= (1L << shift);
            }

            iteration >>>= 1;
            mask &= (mask - 1); // used in Kernighan's bit count algorithm
            // it pops out the least significant bit in the number
        }

        return blockers;
    }

    private static long calculateMagic(int square, int bits, long mask, BiFunction<Integer, Long, Long> attacks) {
        long magic;
        Random r = new Random(123);

        long[]  a = new long[4096],
                b = new long[4096],
                used = new long[4096];

        int permutations = 1 << Long.bitCount(mask);


        for (int i = 0; i < permutations; i++) {
            b[i] = blockersPermutation(i, mask);
            a[i] = attacks.apply(square, b[i]);
        }

        for (int k = 0; k < 100_000_000; k++) {
            magic = randomFewBits(r);
            if (Long.bitCount((mask * magic) & 0xFF00000000000000L) < 5) continue;
            for (int i = 0; i < 4096; i++) used[i] = 0; // Clearing the used ones

            boolean failed = false;
            for (int i = 0; i < permutations; i++) {
                int j = (int)((b[i] * magic) >>> (64 - bits));

                if (used[j] == 0) used[j] = a[i];
                else if (used[j] != a[i]) failed = true;
            }
            if (!failed) return magic;
        }

        throw new IllegalStateException("Magic calculation failed");
    }


    // https://www.chessprogramming.org/index.php?title=Looking_for_Magics
    public static long[] calculateMagics(int[] bits, long[] masks, BiFunction<Integer, Long, Long> attacks) {

        long[] magics = new long[64];

        for (int i = 0; i < 64; i++) {
            magics[i] = calculateMagic(i, bits[i], masks[i], attacks);
        }

        return magics;
    }
}
