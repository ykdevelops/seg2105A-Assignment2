package edu.seg2105.edu.server.backend;

import ocsf.server.*;
import java.io.IOException;

/**
 * This class provides additional functionality to the server.
 */
public class EchoServer extends AbstractServer {
  final public static int DEFAULT_PORT = 5555;
  private int customPort; // Custom port variable to manage port changes

  public EchoServer(int port) {
    super(port);
    this.customPort = port; // Store the custom port locally
  }

  public void quit() {
    try {
      close();
      System.out.println("Server closed.");
    } catch (IOException e) {
      System.out.println("Error closing server.");
    }
    System.exit(0);
  }

  public void stopListeningForClients() {
    stopListening();
    System.out.println("Server stopped listening for new clients.");
  }

  public void closeServer() {
    try {
      close();
      System.out.println("Server closed and all clients disconnected.");
    } catch (IOException e) {
      System.out.println("Error closing the server.");
    }
  }

  public void setCustomPort(int port) { // Renamed to avoid conflict
    if (!isListening() && getNumberOfClients() == 0) {
      this.customPort = port; // Update custom port variable
      System.out.println("Port set to " + port);
    } else {
      System.out.println("Cannot change port while server is open or clients are connected.");
    }
  }

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

  public void displayCurrentPort() {
    System.out.println("Current port: " + this.customPort); // Use custom port variable
  }

  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    System.out.println("Message received: " + msg + " from " + client);
    this.sendToAllClients(msg);
  }

  public void handleMessageFromServerConsole(String message) {
    String serverMessage = "SERVER MSG> " + message;
    System.out.println(serverMessage);
    sendToAllClients(serverMessage);
  }

  protected void serverStarted() {
    System.out.println("Server listening for connections on port " + getPort());
  }

  protected void serverStopped() {
    System.out.println("Server has stopped listening for connections.");
  }

  protected void clientConnected(ConnectionToClient client) {
    System.out.println("A new client has connected: " + client);
  }

  synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("A client has disconnected: " + client);
  }

  public static void main(String[] args) {
    int port = DEFAULT_PORT;
    try {
      port = Integer.parseInt(args[0]);
    } catch(Throwable t) {
      port = DEFAULT_PORT;
    }

    EchoServer sv = new EchoServer(port);
    ServerConsole serverConsole = new ServerConsole(sv);
    new Thread(serverConsole).start();

    try {
      sv.listen();
      sv.serverStarted();
    } catch (Exception ex) {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
// End of EchoServer class
