package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class provides additional functionality to the client.
 * It now includes a login id that is sent with each message.
 *
 * @autor Dr Timothy C. Lethbridge
 * @autor Dr Robert Lagani&egrave;
 * @autor Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient {

  // Instance variables **********************************************

  private ChatIF clientUI;
  private boolean isClosing = false;
  private boolean isLoggingOff = false; // Flag for logoff
  private String loginId; // New field for login ID

  // Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param loginId The login ID of the client.
   * @param host The server host.
   * @param port The server port.
   * @param clientUI The client interface.
   * @throws IOException If an I/O error occurs.
   */
  public ChatClient(String loginId, String host, int port, ChatIF clientUI) 
    throws IOException {
    super(host, port);
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
    sendLoginMessage(); // Send #login <loginId> message after connection
    clientUI.display("Connected to server as " + loginId);
  }

  /**
   * Sends the #login <loginId> message to the server after connection.
   */
  private void sendLoginMessage() {
    try {
      sendToServer("#login " + loginId); // Send login message to server
    } catch (IOException e) {
      clientUI.display("Error: Unable to send login message to the server.");
      quit(); // Quit if unable to send login message
    }
  }

  // Command Methods *************************************************

  /**
   * Gracefully quits the client by closing the connection.
   */
  public void quit() {
    try {
      closeConnection();
    } catch(IOException e) {
      System.out.println("DEBUG: IOException while quitting.");
    }
    System.exit(0);
  }

  /**
   * Logs off from the server.
   */
  public void logoff() {
    try {
      isLoggingOff = true; // Set flag before disconnecting
      closeConnection();
      clientUI.display("Logged off from server.");
    } catch(IOException e) {
      clientUI.display("Error logging off.");
    } finally {
      isLoggingOff = false; // Reset flag after disconnecting
    }
  }

  /**
   * Updates the host, if not connected.
   * 
   * @param host The new host.
   */
  public void updateHost(String host) {
    if (!isConnected()) {
      super.setHost(host);
      clientUI.display("Host set to " + host);
    } else {
      clientUI.display("Cannot change host while connected.");
    }
  }

  /**
   * Updates the port, if not connected.
   * 
   * @param port The new port.
   */
  public void updatePort(int port) {
    if (!isConnected()) {
      super.setPort(port);
      clientUI.display("Port set to " + port);
    } else {
      clientUI.display("Cannot change port while connected.");
    }
  }

  /**
   * Logs back into the server if disconnected.
   */
  public void login() {
    if (!isConnected()) {
      try {
        openConnection();
        sendLoginMessage(); // Send login message after reconnecting
        clientUI.display("Connected to server.");
      } catch(IOException e) {
        clientUI.display("Unable to connect to server.");
      }
    } else {
      clientUI.display("Already connected.");
    }
  }

  /**
   * Returns the current host.
   */
  public String getCurrentHost() {
    return super.getHost();
  }

  /**
   * Returns the current port.
   */
  public int getCurrentPort() {
    return super.getPort();
  }

  // Instance methods ************************************************

  /**
   * Handles a message from the server.
   * 
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) {
    clientUI.display(msg.toString());
  }

  /**
   * Handles a message from the client UI.
   * 
   * @param message The message to send to the server.
   */
  public void handleMessageFromClientUI(String message) {
    try {
      sendToServer(loginId + ": " + message); // Prepend loginId to each message
    } catch(IOException e) {
      clientUI.display("Could not send message to server. Terminating client.");
      closeClient();
    }
  }

  @Override
  protected void connectionClosed() {
    if (!isLoggingOff) { // Only display shutdown message if not logging off
      clientUI.display("The server has shut down.");
      closeClient();
    }
    isLoggingOff = false; // Reset logoff flag
  }

  @Override
  protected void connectionException(Exception exception) {
    if (!isClosing) {
      isClosing = true;
      clientUI.display("Connection error: " + exception.getMessage());
      clientUI.display("The server has shut down unexpectedly.");
      closeClient();
    }
  }

  /**
   * Closes the client connection and exits.
   */
  private void closeClient() {
    try {
      closeConnection();
    } catch (IOException e) {
      System.out.println("DEBUG: IOException in closeClient - connection already closed.");
    }
    System.exit(0);
  }
}

