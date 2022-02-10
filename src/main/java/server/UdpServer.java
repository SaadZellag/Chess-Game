package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer implements Runnable{

    @Override
    public void run() {
        try {
            //Keep a socket open to listen to all the UDP traffic that is destined for this port
            DatagramSocket socket = new DatagramSocket(6969, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (true) {
                System.out.println(getClass().getName() + " > Waiting for packet request...");

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                String message = new String(packet.getData()).trim();

                //Packet received
                System.out.println(getClass().getName() + " > Discovery packet received from: " + packet.getAddress().getHostAddress());
                System.out.println(getClass().getName() + " > Packet received; data: " + message);

                //See if the packet holds the right command (message)
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(getClass().getName() + " > Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static UdpServer getInstance() {
        return DiscoveryThreadHolder.INSTANCE;
    }

    public static void main(String[] args) {
        Thread dt = new Thread(UdpServer.getInstance());
        dt.start();
    }

    private static class DiscoveryThreadHolder {
        private static final UdpServer INSTANCE = new UdpServer();
    }
}
