package lt.escape.labyrinth.client;

import java.io.IOException;

public class NetworkTest {
    public static void main(String[] args) {
        NetworkClient networkClient = new NetworkClient();
        try {
            networkClient.connect("localhost", 5000);
            System.out.println("Connection successful");
        } catch (IOException e) {
            System.out.println("Could not connect to server");
            e.printStackTrace();
        }
    }
}