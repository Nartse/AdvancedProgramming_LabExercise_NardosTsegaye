package server;

import shared.Message;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    public ObjectOutputStream out;
    private ObjectInputStream in;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message msg = (Message) in.readObject();
                System.out.println("DEBUG: Server Handler received: " + msg.getText());
                // Send directly to Server's screen display UI instead of broadcasting
                ServerFXApp.broadcast(msg);
                ServerFXApp.displayMessageOnServer(msg);
            }
        } catch (Exception e) {
            ServerFXApp.clients.remove(this);
            ServerFXApp.displayMessageOnServer(new Message("System", "A client has disconnected.", null));
        } finally {
            try { socket.close(); } catch (Exception ignored) {}
        }
    }
}