package engine.internal;

public interface BitUtils {

    static String boardToString(long board) {
        StringBuilder builder = new StringBuilder(9*8);

        for (int i = 63; i >= 0; i--) {
            if ((board & 1L << i) != 0) {
                builder.append('1');
            } else {
                builder.append('0');
            }
            if (i % 8 == 0) {
                builder.append('\n');
            }
        }

        return builder.toString();
    }

    static long stringToBoard(String board) {
        return Long.parseUnsignedLong(board.replace("\n", ""), 2);
    }

    static long debugBoard(String message, long board) {
        System.out.println(message + ":\n" + boardToString(board));
        return board;
    }
}
