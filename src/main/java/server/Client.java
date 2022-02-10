package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private static final int PORT = 6969;
    String hostIP;

    public void startConnection() throws IOException {
        ArrayList<String> hostArray = new UdpFinder().findServer();

        if (hostArray.size() > 1) {
            //TODO: Let player choose which host they want
            hostIP = hostArray.get(0); //This is temporary
        } else {
            hostIP = hostArray.get(0);
        }

        clientSocket = new Socket(hostIP, PORT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
