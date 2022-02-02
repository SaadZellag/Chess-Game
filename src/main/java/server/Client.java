package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final int PORT = 6969;

    public void startConnection() throws IOException {
        List<String> hosts = getHosts();
        int choices = hosts.size();
        int choice;
        String host;

        //Select which host
        if (choices > 1) {
            Scanner in = new Scanner(System.in);
            do {
                choice = in.nextInt();
                host = hosts.get(choice);
            } while (choice < 1 || choice > choices);
        } else {
            host = hosts.get(0);
        }
        clientSocket = new Socket(host, 6969);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    //Get hosts listening on port 6969
    private static List<String> getHosts() throws IOException {
        List<String> hosts = new ArrayList<>();
        for (String ip : ShowIP.getIpArray()) {
            if (serverListening(ip)) {
               hosts.add(ip);
            }
        }
        return hosts;
    }

    //Check if server is open on port 6969
    private static boolean serverListening(String host)
    {
        Socket s = null;
        int t = 1000;
        try {
            s = new Socket();
            s.connect(new InetSocketAddress(host, PORT), t);
            return true;
        } catch (Exception e) {
            return false;
        }
        finally {
            if(s != null)
                try {s.close();}
                catch(Exception e){
                    e.printStackTrace();
                }
        }
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        return in.readLine();
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
