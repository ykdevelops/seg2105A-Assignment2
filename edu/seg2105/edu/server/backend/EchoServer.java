package edu.seg2105.edu.server.backend;

import ocsf.server.*;
import java.io.IOException;

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

      // Check if the message is a login command (with or without extra text after "#login")
      if (message.startsWith("#login ")) {
          // Check if the client has already logged in
          if (client.getInfo("loginId") == null) {
              // First #login command: Extract loginId from the message
              String loginId = message.substring(7).trim();
              client.setInfo("loginId", loginId); // Save the loginId for future reference
              System.out.println(loginId + " has logged in.");
              this.sendToAllClients(loginId + " has joined the chat.");
          } else {
              // If #login is received after the initial login, disconnect the client
              try {
                  client.sendToClient("Error - Second login detected"); // Inform client of error
                  String loginId = (String) client.getInfo("loginId");
                  System.out.println(loginId + " attempted to log in while already logged in. " + loginId + " has logged out."); // Print the desired message
                  client.close(); // Terminate the connection
              } catch (IOException e) {
                  System.out.println("Error closing connection with client.");
              }
          }
      } else {
          // Handle the message sent from the client indicating the attempted re-login
          if (message.contains("attempted to log in while already logged in")) {
              System.out.println(message); // Print the message received from the client
          } else {
              // For non-login messages, check if loginId has been set
              String loginId = (String) client.getInfo("loginId");
              if (loginId == null) {
                  // If loginId is null, client hasn't logged in correctly, disconnect them
                  try {
                      client.sendToClient("ERROR: You must log in first with #login <loginId>.");
                      client.close(); // Terminate the connection
                      System.out.println("Client sent a message without logging in; connection terminated.");
                  } catch (IOException e) {
                      System.out.println("Error closing connection with client.");
                  }
              } else {
                  // Broadcast the message prefixed with the loginId
                  System.out.println("Message received from " + loginId + ": " + message);
                  this.sendToAllClients(loginId + ": " + message);
              }
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
    if (loginId != null) {
      System.out.println(loginId + " has disconnected.");
      this.sendToAllClients(loginId + " has left the chat.");
    } else {
      System.out.println("An unidentified client has disconnected.");
    }
  }

  // Method to stop listening for new clients
  public void stopListeningForClients() {
    stopListening();
    System.out.println("Server stopped listening for new clients.");
  }

  // Method to close the server and disconnect all current clients
  public void closeServer() {
    try {
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
    sendToAllClients("SERVER MSG> " + message);
    System.out.println("SERVER MSG> " + message);
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
    int port = DEFAULT_PORT;

    try {
      port = Integer.parseInt(args[0]);
    } catch (Throwable t) {
      System.out.println("No port specified. Using default port: " + DEFAULT_PORT);
    }

    EchoServer sv = new EchoServer(port);
    
    try {
      sv.listen(); // Start listening for clients
      System.out.println("Server listening for connections on port " + port);
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
