package engine.internal.pieces;

public interface King {

    // Lookup table
    // Should not be modified
    long[] ATTACKS = {
            0b0100000011000000000000000000000000000000000000000000000000000000L,
            0b1010000011100000000000000000000000000000000000000000000000000000L,
            0b0101000001110000000000000000000000000000000000000000000000000000L,
            0b0010100000111000000000000000000000000000000000000000000000000000L,
            0b0001010000011100000000000000000000000000000000000000000000000000L,
            0b0000101000001110000000000000000000000000000000000000000000000000L,
            0b0000010100000111000000000000000000000000000000000000000000000000L,
            0b0000001000000011000000000000000000000000000000000000000000000000L,
            0b1100000001000000110000000000000000000000000000000000000000000000L,
            0b1110000010100000111000000000000000000000000000000000000000000000L,
            0b0111000001010000011100000000000000000000000000000000000000000000L,
            0b0011100000101000001110000000000000000000000000000000000000000000L,
            0b0001110000010100000111000000000000000000000000000000000000000000L,
            0b0000111000001010000011100000000000000000000000000000000000000000L,
            0b0000011100000101000001110000000000000000000000000000000000000000L,
            0b0000001100000010000000110000000000000000000000000000000000000000L,
            0b0000000011000000010000001100000000000000000000000000000000000000L,
            0b0000000011100000101000001110000000000000000000000000000000000000L,
            0b0000000001110000010100000111000000000000000000000000000000000000L,
            0b0000000000111000001010000011100000000000000000000000000000000000L,
            0b0000000000011100000101000001110000000000000000000000000000000000L,
            0b0000000000001110000010100000111000000000000000000000000000000000L,
            0b0000000000000111000001010000011100000000000000000000000000000000L,
            0b0000000000000011000000100000001100000000000000000000000000000000L,
            0b0000000000000000110000000100000011000000000000000000000000000000L,
            0b0000000000000000111000001010000011100000000000000000000000000000L,
            0b0000000000000000011100000101000001110000000000000000000000000000L,
            0b0000000000000000001110000010100000111000000000000000000000000000L,
            0b0000000000000000000111000001010000011100000000000000000000000000L,
            0b0000000000000000000011100000101000001110000000000000000000000000L,
            0b0000000000000000000001110000010100000111000000000000000000000000L,
            0b0000000000000000000000110000001000000011000000000000000000000000L,
            0b0000000000000000000000001100000001000000110000000000000000000000L,
            0b0000000000000000000000001110000010100000111000000000000000000000L,
            0b0000000000000000000000000111000001010000011100000000000000000000L,
            0b0000000000000000000000000011100000101000001110000000000000000000L,
            0b0000000000000000000000000001110000010100000111000000000000000000L,
            0b0000000000000000000000000000111000001010000011100000000000000000L,
            0b0000000000000000000000000000011100000101000001110000000000000000L,
            0b0000000000000000000000000000001100000010000000110000000000000000L,
            0b0000000000000000000000000000000011000000010000001100000000000000L,
            0b0000000000000000000000000000000011100000101000001110000000000000L,
            0b0000000000000000000000000000000001110000010100000111000000000000L,
            0b0000000000000000000000000000000000111000001010000011100000000000L,
            0b0000000000000000000000000000000000011100000101000001110000000000L,
            0b0000000000000000000000000000000000001110000010100000111000000000L,
            0b0000000000000000000000000000000000000111000001010000011100000000L,
            0b0000000000000000000000000000000000000011000000100000001100000000L,
            0b0000000000000000000000000000000000000000110000000100000011000000L,
            0b0000000000000000000000000000000000000000111000001010000011100000L,
            0b0000000000000000000000000000000000000000011100000101000001110000L,
            0b0000000000000000000000000000000000000000001110000010100000111000L,
            0b0000000000000000000000000000000000000000000111000001010000011100L,
            0b0000000000000000000000000000000000000000000011100000101000001110L,
            0b0000000000000000000000000000000000000000000001110000010100000111L,
            0b0000000000000000000000000000000000000000000000110000001000000011L,
            0b0000000000000000000000000000000000000000000000001100000001000000L,
            0b0000000000000000000000000000000000000000000000001110000010100000L,
            0b0000000000000000000000000000000000000000000000000111000001010000L,
            0b0000000000000000000000000000000000000000000000000011100000101000L,
            0b0000000000000000000000000000000000000000000000000001110000010100L,
            0b0000000000000000000000000000000000000000000000000000111000001010L,
            0b0000000000000000000000000000000000000000000000000000011100000101L,
            0b0000000000000000000000000000000000000000000000000000001100000010L,
    };

    static long getAttack(final long king) {
        return ATTACKS[Long.numberOfLeadingZeros(king)];
    }

}