package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class IdGenerator {

    /*
    Usage:

    IdGenerator gen = new IdGenerator();
    gen.ipToId("192.168.1.44"); [returns azure buckaroo]

     */

    String[] wordArray;
    String ip;

    public IdGenerator() {
        try {
            BufferedReader wordList = new BufferedReader(new InputStreamReader(GameServer.getResourceStream("server/words.txt")));
            Scanner reader = new Scanner(wordList);
            StringBuilder sb = new StringBuilder();
            while (reader.hasNext()) {
                sb.append(reader.nextLine());
                wordArray = sb.toString().split(",");
            }

            //Gets local IP, not loopback address
            Socket s = new Socket("www.google.com", 80);
            ip = s.getLocalAddress().getHostAddress();
            s.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String[] getWordArray() {
        return wordArray;
    }

    public String getIp() {
        return ip;
    }

    /*
    Takes last two bytes of ip and indexes into words list to
    generate a more readable hostname.
     */
    public String ipToID(String ip) {
        ArrayList<Integer> bytes = new ArrayList<>();
        for (String s : ip.split("\\.")) {
            bytes.add(Integer.parseInt(s));
        }
        String s=wordArray[bytes.get(2)] + " " + wordArray[bytes.get(3)];
        return s.toUpperCase();
    }
}
