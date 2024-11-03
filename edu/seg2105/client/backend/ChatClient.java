package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @autor Dr Timothy C. Lethbridge
 * @autor Dr Robert Lagani&egrave;
 * @autor Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient {
  
  // Instance variables **********************************************
  
  ChatIF clientUI;
  private boolean isClosing = false;
  private boolean isLoggingOff = false; // Flag for logoff

  // Constructors ****************************************************
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException {
    super(host, port);
    this.clientUI = clientUI;
    openConnection();
    System.out.println("DEBUG: Client successfully connected to server on port " + port);
  }

  // Command Methods *************************************************
  
  public void quit() {
    try {
      closeConnection();
    } catch(IOException e) {
      System.out.println("DEBUG: IOException while quitting.");
    }
    System.exit(0);
  }

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

  public void updateHost(String host) {
    if (!isConnected()) {
      super.setHost(host);
      clientUI.display("Host set to " + host);
    } else {
      clientUI.display("Cannot change host while connected.");
    }
  }

  public void updatePort(int port) {
    if (!isConnected()) {
      super.setPort(port);
      clientUI.display("Port set to " + port);
    } else {
      clientUI.display("Cannot change port while connected.");
    }
  }

  public void login() {
    if (!isConnected()) {
      try {
        openConnection();
        clientUI.display("Connected to server.");
      } catch(IOException e) {
        clientUI.display("Unable to connect to server.");
      }
    } else {
      clientUI.display("Already connected.");
    }
  }

  public String getCurrentHost() {
    return super.getHost();
  }

  public int getCurrentPort() {
    return super.getPort();
  }

  // Instance methods ************************************************
  
  public void handleMessageFromServer(Object msg) {
    clientUI.display(msg.toString());
  }

  public void handleMessageFromClientUI(String message) {
    try {
      sendToServer(message);
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

  private void closeClient() {
    try {
      closeConnection();
    } catch (IOException e) {
      System.out.println("DEBUG: IOException in closeClient - connection already closed.");
    }
    System.exit(0);
  }
}
