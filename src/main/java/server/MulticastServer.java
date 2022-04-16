package server;

import java.io.IOException;
import java.net.*;

public class MulticastServer extends Thread{
    @Override
    public void run() {
        MulticastSocket socket = null;
        InetAddress mcastaddr;

        try {

            /*
             *    The multicast server runs on port 6970 to leave 6969 free
             *    for the GameServer socket
             */

            socket = new MulticastSocket(6970);

            //Multicast address is 230.0.0.0
            mcastaddr = InetAddress.getByName("230.0.0.0");

            while (true) {
                byte[] sendData = "200".getBytes();
                //Send a response
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, mcastaddr, 6970);
                socket.send(sendPacket);
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Thread t = new Thread(new MulticastServer());
        t.start();
    }
}
