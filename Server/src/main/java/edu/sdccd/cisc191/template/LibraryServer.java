package edu.sdccd.cisc191.template;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryServer {
    private static final Logger LOGGER = Logger.getLogger(LibraryServer.class.getName());
    private final List<String> books;

    public LibraryServer() {
        this.books = new ArrayList<>();
        // Add books to the list
        books.add("The Great Gatsby");
        books.add("1984");
        books.add("To Kill a Mockingbird");
        books.add("Pride and Prejudice");
        books.add("Moby Dick");
        books.add("The Catcher in the Rye");
        books.add("The Hobbit");
        books.add("War and Peace");
        books.add("The Brothers Karamazov");
        books.add("Crime and Punishment");
        books.add("Brave New World");
        books.add("The Road");
        books.add("Don Quixote");
        books.add("The Odyssey");
        books.add("One Hundred Years of Solitude");
        books.add("Anna Karenina");
        books.add("Dracula");
        books.add("The Picture of Dorian Gray");
        books.add("The Adventures of Sherlock Holmes");
        books.add("The Lord of the Rings");
        books.add("The Alchemist");
        books.add("Les Misérables");
        books.add("Harry Potter and the Sorcerer's Stone");
        books.add("The Hunger Games");
        books.add("The Book Thief");
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Library Server started on port " + port);

            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error accepting client connection", e);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error starting the server", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String requestJson = in.readLine();
            BookRequest request = BookRequest.fromJSON(requestJson);

            BookResponse response = processRequest(request);
            out.println(BookResponse.toJSON(response));

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error communicating with client", e);
        }
    }

    private BookResponse processRequest(BookRequest request) {
        switch (request.getAction()) {
            case "LIST":
                return new BookResponse("SUCCESS", String.join(", ", books));
            case "ADD":
                books.add(request.getBookName());
                return new BookResponse("SUCCESS", "Book added: " + request.getBookName());
            case "CHECKOUT":
                if (books.remove(request.getBookName())) {
                    return new BookResponse("SUCCESS", "Book checked out: " + request.getBookName());
                } else {
                    return new BookResponse("ERROR", "Book not available: " + request.getBookName());
                }
            case "RETURN":
                books.add(request.getBookName());
                return new BookResponse("SUCCESS", "Book returned: " + request.getBookName());
            default:
                return new BookResponse("ERROR", "Invalid action: " + request.getAction());
        }
    }

    public static void main(String[] args) {
        try {
            new LibraryServer().start(4444);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Fatal error in the server", e);
        }
    }
}
