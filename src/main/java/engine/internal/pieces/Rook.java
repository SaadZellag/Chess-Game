package engine.internal.pieces;

import engine.internal.Rays;

public class Rook {

    private Rook() {}

    public static final long[] MAGIC_NUMBERS = {
            0x0280002040039088L, 0x82C000C020003004L, 0xC200084200208410L, 0xC200084200208410L, 0x0080020800800400L, 0x4100020400480100L, 0x84001000840A5803L, 0x8080010008204880L,
            0x0180800228824000L, 0x1801400060015000L, 0x4230802000801000L, 0x0040801002080280L, 0x8010804801040080L, 0x801380800C000A00L, 0x0000808002000100L, 0x48C0800880047100L,
            0x0040908000400020L, 0x0510004040002000L, 0x0020010018402100L, 0x20480A00120040A1L, 0x0448018004008088L, 0x408D010008040082L, 0x0010040012081110L, 0x4000020020409401L,
            0x0080004C40042000L, 0x0510004040002000L, 0x80AC100080802000L, 0x0010068080100800L, 0x01C0080080800400L, 0x4000040801102040L, 0x8105002100420004L, 0x8148174600088415L,
            0x2080004000802083L, 0x0210082000401040L, 0x4230802000801000L, 0x8400805004800800L, 0x01C0080080800400L, 0x009E003006000805L, 0x0014181204000110L, 0x0000009042000C05L,
            0x0003C00230848000L, 0x3150002000414001L, 0x4050001020008080L, 0x008100201001000AL, 0x0004000408008080L, 0x9040204010080104L, 0x0000080201040010L, 0x80A1C08044020001L,
            0x0E00488900220200L, 0x0210082000401040L, 0x0020010018402100L, 0x0040801002080280L, 0x0004000408008080L, 0x0005020004008080L, 0x0000100248190400L, 0xA800040881004600L,
            0x00800923001080C1L, 0x2430400180150125L, 0x4000A00009130041L, 0x0001E05500683001L, 0x0002006008041142L, 0x0046000841301C22L, 0x8000020108009004L, 0xC000010054092082L,
    };

    private static final long[] MASKS = new long[64];

    private static final int[] BITS = {
            12, 11, 11, 11, 11, 11, 11, 12,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            11, 10, 10, 10, 10, 10, 10, 11,
            12, 11, 11, 11, 11, 11, 11, 12
    };

    private static final long[][] ATTACKS = new long[64][4096];

    static {

        // Generating Rook masks
        for (int i = 0; i < 64; i++) {
            int rank = i / 8, file = i % 8;
            long mask = 0;

            for (int r = rank+1; r < 7; r++) mask |= (1L << (file + r*8)); // North
            for (int r = rank-1; r > 0; r--) mask |= (1L << (file + r*8)); // South
            for (int f = file+1; f < 7; f++) mask |= (1L << (f + rank*8)); // East
            for (int f = file-1; f > 0; f--) mask |= (1L << (f + rank*8)); // West

            MASKS[i] = mask & ~(1L << i);
        }


        // Getting magic numbers
        // Already done
//        MAGIC_NUMBERS = Rays.calculateMagics(BITS, MASKS, Rook::calculateAttacks);

    }

    // Casting a "ray" from the piece until it hits a blocker
    private static long calculateAttacks(int square, long blockers) {
        long result = 0;
        int rank = square / 8, file = square % 8;

        for (int r = rank+1; r <= 7; r++) {
            long mask = 1L << (file + r*8); // North
            result |= mask;
            if ((blockers & mask) != 0) break;
        }
        for (int r = rank-1; r >= 0; r--) {
            long mask = 1L << (file + r*8); // South
            result |= mask;
            if ((blockers & mask) != 0) break;
        }
        for (int f = file+1; f <= 7; f++) {
            long mask = 1L << (f + rank*8); // East
            result |= mask;
            if ((blockers & mask) != 0) break;
        }
        for (int f = file-1; f >= 0; f--) {
            long mask = 1L << (f + rank*8); // West
            result |= mask;
            if ((blockers & mask) != 0) break;
        }

        return result & ~(1L << square);
    }

    static {
        // Calculating attacks for all positions
        for (int square = 0; square < 64; square++) {
            // For all possible blockers for this square
            for (int blockerIndex = 0; blockerIndex < (1 << BITS[square]); blockerIndex++) {
                long blockers = Rays.blockersPermutation(blockerIndex, MASKS[square]);
                int key = (int) ((blockers * MAGIC_NUMBERS[square]) >>> (64 - BITS[square]));
                ATTACKS[square][key] = calculateAttacks(square, blockers);
            }
        }

    }

    public static long getAttack(final int square, final long blockers) {
        return ATTACKS[square][(int) (((blockers & MASKS[square]) * MAGIC_NUMBERS[square]) >>> (64 - BITS[square]))];
    }

    public static long getAttacks(long mask, final long blockers) {
        int square;
        long attacks = 0;
        while ((square = Long.numberOfTrailingZeros(mask)) != 64) {
            attacks |= getAttack(square, blockers);
            mask &= mask-1;
        }
        return attacks;
    }
}
