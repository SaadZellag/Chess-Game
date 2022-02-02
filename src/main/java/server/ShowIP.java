//Program to handle the shell script and return a nice ArrayList of IPs

package server;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ShowIP
{
    public static List<String> getIpArray() throws IOException {
        
        String path = null;

        //Get proper script for os
        OsUtils.OS os = OsUtils.getOS();
        String ext;
        switch (os) {
            case UNIX -> {
                ext = "sh";
            }
            case WINDOWS -> {
                ext = "bat";
            }
            default -> throw new IllegalStateException("Unexpected value: " + os);
        }

        try {
            path = ShowIP.class.getClassLoader().getResource("serverResources/arp." + ext).getPath();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Create operating system process from arp.sh file command
        ProcessBuilder pb = new ProcessBuilder(path);

        // Starts a new process using the attributes of this process builder
        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder builder = new StringBuilder();
        List<String> ipArray = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null) {
            ipArray.add(line);
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }

        return ipArray;
    }

    public static void main(String[] args) {
        try {
            List<String> a = ShowIP.getIpArray();
            System.out.println(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}