package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;

public class MulticastFinder {

    public HashMap<String, String> multicast() {
        HashMap<String, String> ips = new HashMap<>();
        try {
            InetAddress mcastaddr = InetAddress.getByName("230.0.0.0");

            // The multicast server runs on port 6970 to leave 6969 free
            // for the GameServer socket
            InetSocketAddress group = new InetSocketAddress(mcastaddr, 6970);
            NetworkInterface networkInterface = NetworkInterface.getByName("en0");
            MulticastSocket socket = new MulticastSocket(6970);
            socket.joinGroup(group, networkInterface);

            socket.setSoTimeout(100);
            DatagramPacket recPacket = null;

            for (int i = 0; i < 10; i++) {
                byte[] recvBuf = new byte[512];
                recPacket = new DatagramPacket(recvBuf, recvBuf.length);
                try {
                    socket.receive(recPacket);
                    ips.put(recPacket.getAddress().getHostAddress(), recPacket.getAddress().getCanonicalHostName());

                } catch (SocketTimeoutException ignored) {}
            }

            socket.leaveGroup(group, networkInterface);
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return ips;
    }
}
