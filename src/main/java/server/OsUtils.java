//To determine which shell script to run depending on OS
package server;

public class OsUtils {

    public enum OS {
        WINDOWS, UNIX
    }// Operating systems.

    private static OS os = null;

    public static OS getOS() {
        if (os == null) {
            String operSys = System.getProperty("os.name").toLowerCase();
            if (operSys.contains("win")) {
                os = OS.WINDOWS;
            } else if (operSys.contains("nix") || operSys.contains("nux")
                    || operSys.contains("aix") || operSys.contains("mac")) {
                os = OS.UNIX;
            }
        }
        return os;
    }
}
