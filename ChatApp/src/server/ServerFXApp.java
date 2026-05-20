package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import shared.Message;

import java.io.ByteArrayInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerFXApp extends Application {

    public static Vector<ClientHandler> clients = new Vector<>();
    private static VBox chatBox = new VBox(10);

    @Override
    public void start(Stage stage) {
        chatBox.setStyle("-fx-padding: 15;");
        
        ScrollPane scrollPane = new ScrollPane(chatBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        TextField serverInputField = new TextField();
        serverInputField.setPromptText("Type a message to send to all clients...");
        Button sendBtn = new Button("Send to Clients");
        HBox inputBox = new HBox(10, serverInputField, sendBtn);
        HBox.setHgrow(serverInputField, Priority.ALWAYS);

        VBox root = new VBox(10, new Label("Server Control Hub"), scrollPane, inputBox);
        root.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Chat Server (Display Hub)");
        stage.setScene(scene);
        stage.show();

        // Server sending to clients
        sendBtn.setOnAction(e -> {
            String text = serverInputField.getText().trim();
            if (!text.isEmpty()) {
                Message msg = new Message("Server", text, null);
                
                // Show it on our own Server UI screen too!
                displayMessageOnServer(msg);
                
                broadcast(msg);
                serverInputField.clear();
                System.out.println("Server broadcasted: " + text);
            }
        });

        new Thread(this::startSocketServer).start();
    }

    private void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            Platform.runLater(() -> chatBox.getChildren().add(new Label("Server live and listening on port 5000...")));
            Database.connect();

            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🌟 UPDATED: Formats and displays all traffic incoming or outgoing
    public static void displayMessageOnServer(Message msg) {
        Platform.runLater(() -> {
            if (msg.getImageData() != null) {
                try {
                    Image image = new Image(new ByteArrayInputStream(msg.getImageData()));
                    ImageView imageView = new ImageView(image);
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);

                    Label label = new Label("📩 Client '" + msg.getSender() + "' sent an image:");
                    label.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    
                    chatBox.getChildren().addAll(label, imageView);
                } catch (Exception ex) {
                    chatBox.getChildren().add(new Label("Error rendering incoming image object."));
                }
            } else {
                Label textLabel = new Label();
                if ("Server".equals(msg.getSender())) {
                    // Format for outbound server messages
                    textLabel.setText("📢 You (Server): " + msg.getText());
                    textLabel.setStyle("-fx-text-fill: #16a085; -fx-font-weight: bold;");
                } else {
                    // Format for incoming client messages
                    textLabel.setText("📩 Client '" + msg.getSender() + "' sent you a message: " + msg.getText());
                    textLabel.setStyle("-fx-text-fill: #2980b9;");
                }
                chatBox.getChildren().add(textLabel);
            }
        });
    }

    public static void broadcast(Message msg) {
        for (ClientHandler client : clients) {
            try {
                client.out.writeObject(msg);
                client.out.flush();
                client.out.reset();
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}