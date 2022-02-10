package server;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class UdpFinder {

    public ArrayList<String> findServer() {
        Set<String> hostSet = new HashSet<>();
        ArrayList<Thread> threadArrayList = new ArrayList<>();
        int MAX_HOSTS = 100;

        // Find the server using UDP broadcast
        for (int i = 0; i < MAX_HOSTS; i++) {
            threadArrayList.add(new Thread(() -> {
                try {
                    //Open a random port to send the package
                    DatagramSocket c = new DatagramSocket();
                    c.setBroadcast(true);

                    byte[] sendData = "DISCOVER_FUIFSERVER_REQUEST".getBytes();

                    // Broadcast the message over all the network interfaces
                    Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
                    while (interfaces.hasMoreElements()) {
                        NetworkInterface networkInterface = interfaces.nextElement();

                        if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                            continue; // Don't want to broadcast to the loopback interface
                        }

                        for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            if (broadcast == null) {
                                continue;
                            }

                            // Send the broadcast package!
                            try {
                                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 6969);
                                c.send(sendPacket);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    //Wait for a response
                    byte[] recvBuf = new byte[15000];
                    c.setSoTimeout(100);

                    DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                    try {
                        c.receive(receivePacket);
                        //We have a response
                    } catch (SocketTimeoutException ignored) {}

                    //Check if the message is correct and host is open on 6969
                    String message = new String(receivePacket.getData()).trim();
                    if (message.equals("DISCOVER_FUIFSERVER_RESPONSE") && (receivePacket.getPort() == 6969)) {
                        synchronized (hostSet) {
                            hostSet.add(receivePacket.getAddress().toString().replace("/", ""));
                        }
                    }

                    //Close the port!
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));
            threadArrayList.get(i).start();
        }

        for (Thread t : threadArrayList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>(hostSet);
    }

    public static void main(String[] args) {
        UdpFinder c = new UdpFinder();
        ArrayList<String> h = c.findServer();
        System.out.println("Found " + h.size() + " address(es): \n" + h.get(0));
    }
}
