package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import shared.Message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public class App extends Application {

    private Client client;
    private String username = "Anonymous";
    private VBox chatDisplayBox = new VBox(10); 

    @Override
    public void start(Stage stage) {
        // 1. Get Username
        TextInputDialog dialog = new TextInputDialog("User" + (int)(Math.random() * 1000));
        dialog.setTitle("Login");
        dialog.setHeaderText("Join the Group Chat");
        dialog.setContentText("Enter your name:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            username = result.get().trim();
        } else {
            System.exit(0);
        }

        // 2. Initialize Client
        client = new Client();

        // 3. UI Setup
        chatDisplayBox.setStyle("-fx-padding: 10;");
        ScrollPane scrollPane = new ScrollPane(chatDisplayBox);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        TextField messageField = new TextField();
        messageField.setPromptText("Type a message to everyone...");
        HBox.setHgrow(messageField, Priority.ALWAYS);

        Button sendBtn = new Button("Send Text");
        Button imageBtn = new Button("Send Image");
        HBox inputBox = new HBox(10, messageField, sendBtn, imageBtn);

        VBox root = new VBox(10, new Label("Chat Feed:"), scrollPane, inputBox);
        root.setStyle("-fx-padding: 15;");

        Scene scene = new Scene(root, 450, 500);
        stage.setTitle("Chat Client: " + username);
        stage.setScene(scene);
        stage.show();

        // 4. UPDATED LISTENING THREAD (Handles Server AND other Clients)
        new Thread(() -> {
            try {
                while (true) {
                    Object received = client.getInput().readObject();
                    if (received instanceof Message) {
                        Message msg = (Message) received;
                        
                        Platform.runLater(() -> {
                            // CASE A: It's an Image
                            if (msg.getImageData() != null) {
                                renderIncomingImage(msg);
                            } 
                            // CASE B: It's Text
                            else {
                                renderIncomingText(msg);
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() -> 
                    chatDisplayBox.getChildren().add(new Label("⚠️ Lost connection to server."))
                );
            }
        }).start();

        // 5. Send Actions
        sendBtn.setOnAction(e -> {
            String text = messageField.getText().trim();
            if (!text.isEmpty()) {
                client.sendMessage(new Message(username, text, null));
                messageField.clear(); 
            }
        });

        imageBtn.setOnAction(e -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
                );
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    byte[] imageBytes = Files.readAllBytes(file.toPath());
                    client.sendMessage(new Message(username, null, imageBytes));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    // --- Helper UI Methods to keep the code clean ---

    private void renderIncomingText(Message msg) {
        Label label = new Label();
        if ("Server".equals(msg.getSender())) {
            label.setText("🚨 [SERVER]: " + msg.getText());
            label.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        } else {
            label.setText(msg.getSender() + ": " + msg.getText());
            if (msg.getSender().equals(username)) {
                label.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
            }
        }
        chatDisplayBox.getChildren().add(label);
    }

    private void renderIncomingImage(Message msg) {
        try {
            Image img = new Image(new ByteArrayInputStream(msg.getImageData()));
            ImageView iv = new ImageView(img);
            iv.setFitWidth(200);
            iv.setPreserveRatio(true);
            
            Label nameTag = new Label(msg.getSender() + " shared an image:");
            nameTag.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            
            chatDisplayBox.getChildren().addAll(nameTag, iv);
        } catch (Exception e) {
            chatDisplayBox.getChildren().add(new Label("Failed to load image from " + msg.getSender()));
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}