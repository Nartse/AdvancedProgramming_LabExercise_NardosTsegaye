
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;

public class Notepad extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Notepad - JavaFX");

        textArea = new TextArea();

        // ===== MENU BAR =====
        MenuBar menuBar = new MenuBar();

        // ----- FILE MENU -----
        Menu fileMenu = new Menu("File");

        MenuItem newFile = new MenuItem("New");
        MenuItem openFile = new MenuItem("Open");
        MenuItem saveFile = new MenuItem("Save");
        MenuItem exit = new MenuItem("Exit");

        // ----- EDIT MENU -----
        Menu editMenu = new Menu("Edit");

        MenuItem cut = new MenuItem("Cut");
        MenuItem copy = new MenuItem("Copy");
        MenuItem paste = new MenuItem("Paste");

        // ===== ACTIONS =====

        // New File
        newFile.setOnAction(e -> textArea.clear());

        // Open File
        openFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open File");
            File file = fileChooser.showOpenDialog(stage);

            if (file != null) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    textArea.clear();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        textArea.appendText(line + "\n");
                    }
                } catch (IOException ex) {
                    showError("Error opening file!");
                }
            }
        });

        // Save File
        saveFile.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(textArea.getText());
                } catch (IOException ex) {
                    showError("Error saving file!");
                }
            }
        });

        // Exit
        exit.setOnAction(e -> stage.close());

        // Edit actions
        cut.setOnAction(e -> textArea.cut());
        copy.setOnAction(e -> textArea.copy());
        paste.setOnAction(e -> textArea.paste());

        // ===== ADD MENU ITEMS =====
        fileMenu.getItems().addAll(newFile, openFile, saveFile, exit);
        editMenu.getItems().addAll(cut, copy, paste);

        menuBar.getMenus().addAll(fileMenu, editMenu);

        // ===== LAYOUT =====
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(textArea);

        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.show();
    }

    // ===== ERROR POPUP =====
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}