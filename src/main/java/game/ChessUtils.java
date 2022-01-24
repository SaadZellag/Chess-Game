package game;

import java.util.HashMap;

public class ChessUtils {

    private ChessUtils() {}

    private static final String[] ALGEBRAIC_NOTATION = {
            "a8",
            "b8",
            "c8",
            "d8",
            "e8",
            "f8",
            "g8",
            "h8",
            "a7",
            "b7",
            "c7",
            "d7",
            "e7",
            "f7",
            "g7",
            "h7",
            "a6",
            "b6",
            "c6",
            "d6",
            "e6",
            "f6",
            "g6",
            "h6",
            "a5",
            "b5",
            "c5",
            "d5",
            "e5",
            "f5",
            "g5",
            "h5",
            "a4",
            "b4",
            "c4",
            "d4",
            "e4",
            "f4",
            "g4",
            "h4",
            "a3",
            "b3",
            "c3",
            "d3",
            "e3",
            "f3",
            "g3",
            "h3",
            "a2",
            "b2",
            "c2",
            "d2",
            "e2",
            "f2",
            "g2",
            "h2",
            "a1",
            "b1",
            "c1",
            "d1",
            "e1",
            "f1",
            "g1",
            "h1",
    };

    private static final HashMap<String, Integer> ALGEBRAIC_TO_INDEX = new HashMap<>(64);

    // Initialize the hashmap
    static {
        for (int i = 0; i < 64; i++) {
            ALGEBRAIC_TO_INDEX.put(ALGEBRAIC_NOTATION[i], i);
        }
    }


    public static String indexToAlgebraic(int index) {
        if (index < 0 || index > 63) {
            throw new IllegalArgumentException("Invalid Index Range (0-63)");
        }
        return ALGEBRAIC_NOTATION[index];
    }

    public static int algebraicToIndex(String algebraic) {
        if (algebraic.length() != 2) {
            throw new IllegalArgumentException("Invalid Algebraic Notation Length");
        }
        Integer index = ALGEBRAIC_TO_INDEX.get(algebraic);

        if (index == null) {
            throw new IllegalArgumentException("Invalid Algebraic Notation");
        }
        return index;
    }
}
