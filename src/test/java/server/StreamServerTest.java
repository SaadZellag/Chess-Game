package server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class StreamServerTest {

    static Client client;

    @BeforeAll
    public static void setup() throws IOException {
        client = new Client();
        client.startConnection("127.0.0.1", 6969);
    }

    @Test
    public void streamTest() throws IOException {
        String msg1 = client.sendMessage("This");
        String msg2 = client.sendMessage("is a");
        String msg3 = client.sendMessage("server test");

        //Terminate server
        String endMsg = client.sendMessage(".");

        assertEquals("This", msg1);
        assertEquals("is a", msg2);
        assertEquals("server test", msg3);
        assertEquals("Goodbye.", endMsg);
    }
    
    @AfterAll
    static void tearDown() throws IOException {
        client.stopConnection();
    }
}
