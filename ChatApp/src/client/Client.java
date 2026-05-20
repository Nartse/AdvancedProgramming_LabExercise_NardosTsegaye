package client;

import shared.Message;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {

    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Client() {
        try {
            socket = new Socket("localhost", 5000);

            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); 
            
            input = new ObjectInputStream(socket.getInputStream());
            System.out.println("Connected to server");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) {
    try {
        if (output != null) {
            System.out.println("DEBUG: Client is sending: " + message.getText());
            output.writeObject(message);
            output.flush();
            output.reset(); // This is the most important line for updates!
            System.out.println("Message sent to server...");
        }
    } catch (Exception e) {
        System.out.println("Error sending message: " + e.getMessage());
    }
}

    public ObjectInputStream getInput() {
        return input;
    }
}