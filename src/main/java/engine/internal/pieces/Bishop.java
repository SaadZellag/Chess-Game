package engine.internal.pieces;

public class Bishop {

    private Bishop() {}

    public static final long[] MAGIC_NUMBERS = {
            0x1010040808004010L, 0x0810100080888610L, 0x00080084008010A0L, 0x0004041084000200L, 0x5081104041000000L, 0x0068441220084000L, 0x000482100220400CL, 0x0100440080882001L,
            0x000040450400A204L, 0x0001500202040831L, 0x00810808425080C0L, 0x0840880A00250002L, 0x00A8040420048410L, 0x1010808220601000L, 0x0C4024040C052500L, 0x0000C04304882080L,
            0x00A8040420048410L, 0x00D4000808085440L, 0x2010200204004008L, 0x8014006844120003L, 0x8000811400A00200L, 0x0000800040504000L, 0x30C2080402010460L, 0x1050401082109000L,
            0x4104C00C201A2404L, 0x2008080820021082L, 0x0090A80004014402L, 0x00020800240050A0L, 0x4002840020802002L, 0x04080090A2020080L, 0x000202800414888AL, 0x0904005200804410L,
            0x0004104200084206L, 0x0004104200084206L, 0x0882042220100081L, 0x4043040402280210L, 0x0020118400828020L, 0x0020344100108080L, 0x2602023214040095L, 0x01044100222A0090L,
            0x00A8040420048410L, 0x30C2080402010460L, 0x000F004026001010L, 0x01800A0214000202L, 0x02404851040000C0L, 0x0042040816001021L, 0x0004041084000200L, 0x04080090A2020080L,
            0x000482100220400CL, 0x0C4024040C052500L, 0x0380032404120100L, 0x0000084C2A080084L, 0x4000001002020100L, 0x8000200501120000L, 0x0408100108010080L, 0x0810100080888610L,
            0x0100440080882001L, 0x0000C04304882080L, 0x1400004202090402L, 0x1404040141084802L, 0x0961800050120880L, 0x0408004004880088L, 0x000040450400A204L, 0x1010040808004010L,
    };

    private static final long[] MASKS = new long[64];

    private static final int[] BITS = {
            6, 5, 5, 5, 5, 5, 5, 6,
            5, 5, 5, 5, 5, 5, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 9, 9, 7, 5, 5,
            5, 5, 7, 7, 7, 7, 5, 5,
            5, 5, 5, 5, 5, 5, 5, 5,
            6, 5, 5, 5, 5, 5, 5, 6
    };

    private static final long[][] ATTACKS = new long[64][512];

    static {

        // Generating Bishop masks
        for (int i = 0; i < 64; i++) {
            int rank = i / 8, file = i % 8;
            long mask = 0;
            
            for(int r=rank+1, f=file+1; r<=6 && f<=6; r++, f++) mask |= (1L << (f + r*8));
            for(int r=rank+1, f=file-1; r<=6 && f>=1; r++, f--) mask |= (1L << (f + r*8));
            for(int r=rank-1, f=file+1; r>=1 && f<=6; r--, f++) mask |= (1L << (f + r*8));
            for(int r=rank-1, f=file-1; r>=1 && f>=1; r--, f--) mask |= (1L << (f + r*8));

            MASKS[i] = mask & ~(1L << i);
        }


        // Getting magic numbers
        // Already done
//        MAGIC_NUMBERS = Rays.calculateMagics(BITS, MASKS, Bishop::calculateAttacks);

    }

    private static long calculateAttacks(int square, long blockers) {
        long result = 0;
        int rk = square/8, fl = square%8, r, f;
        for(r = rk+1, f = fl+1; r <= 7 && f <= 7; r++, f++) {
            result |= (1L << (f + r*8));
            if((blockers & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk+1, f = fl-1; r <= 7 && f >= 0; r++, f--) {
            result |= (1L << (f + r*8));
            if((blockers & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk-1, f = fl+1; r >= 0 && f <= 7; r--, f++) {
            result |= (1L << (f + r*8));
            if((blockers & (1L << (f + r * 8))) != 0) break;
        }
        for(r = rk-1, f = fl-1; r >= 0 && f >= 0; r--, f--) {
            result |= (1L << (f + r*8));
            if((blockers & (1L << (f + r * 8))) != 0) break;
        }
        return result;
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

    public static long getAttack(int square, long blockers) {
        blockers &= MASKS[square];
        int key = (int) ((blockers * MAGIC_NUMBERS[square]) >>> (64 - BITS[square]));
        return ATTACKS[square][key];
    }
}
