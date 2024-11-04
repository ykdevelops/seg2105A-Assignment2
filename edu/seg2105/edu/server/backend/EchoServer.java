package edu.seg2105.edu.server.backend;

import ocsf.server.*;
import java.io.IOException;
import java.util.Scanner;
/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @author Youssef Khalil
 */
public class EchoServer extends AbstractServer {
    final public static int DEFAULT_PORT = 5555;
    private int customPort; // to store custom port for displayCurrentPort

    public EchoServer(int port) {
        super(port);
        this.customPort = port;
    }

    @Override
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        String message = msg.toString();
        String loginId = (String) client.getInfo("loginId");

        if (message.startsWith("#login ")) {
            System.out.println("Message received: null : #login");

            if (loginId == null) {
                loginId = message.substring(7).trim();
                client.setInfo("loginId", loginId);
                System.out.println(loginId + " has logged in.");
                this.sendToAllClients(loginId + " has joined the chat.");
            } else {
                try {
                    client.sendToClient("Error - Second login detected");
                    System.out.println(loginId + " attempted to log in while already logged in. " + loginId + " has logged out.");
                    client.close();
                } catch (IOException e) {
                    System.out.println("Error closing connection with client.");
                }
            }
        } else if (message.equals("#quit")) {
            System.out.println("Message received from " + loginId + ": #quit");
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error closing connection with client.");
            }
        } else if (message.equals("#logoff")) {
            System.out.println("Message received from " + loginId + ": #logoff");
            if (loginId != null) {
                System.out.println(loginId + " has logged off.");
                this.sendToAllClients(loginId + " has left the chat.");
                client.setInfo("isLogoff", true); // Set flag indicating this is a logoff
            }
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error closing client connection for " + loginId);
            }
        } else {
            if (loginId == null) {
                try {
                    client.sendToClient("ERROR: You must log in first with #login <loginId>.");
                    client.close();
                    System.out.println("Client sent a message without logging in; connection terminated.");
                } catch (IOException e) {
                    System.out.println("Error closing connection with client.");
                }
            } else {
                System.out.println("Message received from " + loginId + ": " + message);
                this.sendToAllClients(loginId + ": " + message);
            }
        }
    }



    @Override
    protected void clientConnected(ConnectionToClient client) {
        System.out.println("A new client has connected: " + client);
    }

    @Override
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        String loginId = (String) client.getInfo("loginId");
        Boolean isLogoff = (Boolean) client.getInfo("isLogoff");

        if (loginId != null && (isLogoff == null || !isLogoff)) {
            System.out.println(loginId + " has disconnected.");
            this.sendToAllClients(loginId + " has left the chat.");
        }
    }




    // Method to stop listening for new clients
    public void stopListeningForClients() {
        stopListening();
        System.out.println("Server has stopped listening for connections.");
    }

    // Method to close the server and disconnect all current clients
    public void closeServer() {
        try {
            sendToAllClients("The server has shut down.");
            close();
            System.out.println("Server closed and all clients disconnected.");
        } catch (IOException e) {
            System.out.println("Error closing the server.");
        }
    }

    // Method to set a custom port for the server
    public void setCustomPort(int port) {
        if (!isListening() && getNumberOfClients() == 0) {
            this.customPort = port;
            super.setPort(port);
            System.out.println("Port set to " + port);
        } else {
            System.out.println("Cannot change port while server is open or clients are connected.");
        }
    }

    // Method to start the server if it is currently stopped
    public void startServer() {
        if (!isListening()) {
            try {
                listen();
                System.out.println("Server started listening for clients.");
            } catch (IOException e) {
                System.out.println("Error starting server: " + e.getMessage());
            }
        } else {
            System.out.println("Server is already listening.");
        }
    }

    // Method to display the current port of the server
    public void displayCurrentPort() {
        System.out.println("Current port: " + this.customPort);
    }

    // New method to handle messages from the server console
    public void handleMessageFromServerConsole(String message) {
        if (message.equals("#quit")) {
            quit();
        } else if (message.equals("#stop")) {
            stopListeningForClients();
        } else if (message.equals("#close")) {
            closeServer();
        } else if (message.startsWith("#setport ")) {
            try {
                int port = Integer.parseInt(message.substring(9).trim());
                setCustomPort(port);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number.");
            }
        } else if (message.equals("#start")) {
            startServer();
        } else if (message.equals("#displayport")) {
            displayCurrentPort();
        } else {
            sendToAllClients("SERVER MSG> " + message);
            System.out.println("SERVER MSG> " + message);
        }
    }

    // Method to quit the server
    public void quit() {
        try {
            close();
            System.out.println("Server closed.");
        } catch (IOException e) {
            System.out.println("Error closing server.");
        }
        System.exit(0);
    }
    public static void main(String[] args) {
        int port = DEFAULT_PORT; // Default port if none specified

        // Check if a port number is provided as a command-line argument
        try {
            port = Integer.parseInt(args[0]);
        } catch (Throwable t) {
            System.out.println("No port specified. Using default port: " + DEFAULT_PORT);
        }

        EchoServer sv = new EchoServer(port);

        // Try to start the server and listen for client connections
        try {
            sv.listen();
            System.out.println("Server listening for connections on port " + port);
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
            return; // Exit if server fails to start
        }

        // Scanner to handle console input for server commands
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.nextLine();
            sv.handleMessageFromServerConsole(command);

            // Break loop and stop the server if #quit is entered
            if (command.equals("#quit")) {
                break;
            }
        }
        scanner.close(); // Close the scanner resource
    }

}
