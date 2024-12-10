package edu.sdccd.cisc191.template;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryClientGUI extends Application {
    private static final Logger LOGGER = Logger.getLogger(LibraryClientGUI.class.getName());
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private TextArea displayArea; // Displays the books and their statuses
    private Map<String, String> bookStatus; // Tracks the status of books dynamically

    @Override
    public void start(Stage primaryStage) {
        connectToServer();
        bookStatus = new HashMap<>();

        // UI Layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        displayArea = new TextArea();
        displayArea.setEditable(false); // Prevent user edits in the display area

        // UI Buttons and Input Field
        Button listButton = new Button("List Books");
        Button addButton = new Button("Add Book");
        Button checkoutButton = new Button("Check Out Book");
        Button returnButton = new Button("Return Book");

        TextField inputField = new TextField();
        inputField.setPromptText("Enter book name");

        // Button Actions
        listButton.setOnAction(e -> sendRequest("LIST", ""));
        addButton.setOnAction(e -> {
            String bookName = inputField.getText().trim();
            if (!bookName.isEmpty()) {
                sendRequest("ADD", bookName);
                inputField.clear();
            } else {
                showError("Book name cannot be empty.");
            }
        });
        checkoutButton.setOnAction(e -> {
            String bookName = inputField.getText().trim();
            if (!bookName.isEmpty()) {
                sendRequest("CHECKOUT", bookName);
                inputField.clear();
            } else {
                showError("Book name cannot be empty.");
            }
        });
        returnButton.setOnAction(e -> {
            String bookName = inputField.getText().trim();
            if (!bookName.isEmpty()) {
                sendRequest("RETURN", bookName);
                inputField.clear();
            } else {
                showError("Book name cannot be empty.");
            }
        });

        root.getChildren().addAll(listButton, addButton, checkoutButton, returnButton, inputField, displayArea);

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("Library Management System");
        primaryStage.setScene(scene);
        primaryStage.show();

        sendRequest("LIST", ""); // Automatically display the book list on startup
    }

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 4444);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            LOGGER.info("Connected to the server.");
        } catch (IOException e) {
            showError("Could not connect to the server. Ensure the server is running.");
            LOGGER.log(Level.SEVERE, "Error connecting to the server", e);
        }
    }

    private void sendRequest(String action, String bookName) {
        if (socket == null || out == null || in == null) {
            showError("Not connected to the server.");
            return;
        }

        try {
            BookRequest request = new BookRequest(action, bookName);
            out.println(BookRequest.toJSON(request));

            String responseJson = in.readLine();
            if (responseJson != null) {
                BookResponse response = BookResponse.fromJSON(responseJson);

                if ("LIST".equals(action)) {
                    updateBookList(response.getMessage());
                } else if ("ADD".equals(action) || "CHECKOUT".equals(action) || "RETURN".equals(action)) {
                    displayMessage(response.getStatus() + ": " + response.getMessage());
                    if ("SUCCESS".equals(response.getStatus())) {
                        if ("CHECKOUT".equals(action)) {
                            bookStatus.put(bookName, "Checked Out");
                        } else if ("RETURN".equals(action) || "ADD".equals(action)) {
                            bookStatus.put(bookName, "Available");
                        }
                    }
                    sendRequest("LIST", ""); // Refresh the book list
                }
            }
        } catch (IOException e) {
            showError("Error communicating with the server.");
            LOGGER.log(Level.SEVERE, "Error communicating with the server", e);
        }
    }

    private void updateBookList(String books) {
        displayArea.clear();
        if (books == null || books.isEmpty()) {
            displayArea.appendText("No books available.\n");
            return;
        }
        String[] bookArray = books.split(", ");
        for (String book : bookArray) {
            String status = bookStatus.getOrDefault(book, "Available");
            displayArea.appendText(book + " - " + status + "\n");
        }
    }

    private void displayMessage(String message) {
        displayArea.appendText(message + "\n");
    }

    private void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
        if (socket != null) {
            socket.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
