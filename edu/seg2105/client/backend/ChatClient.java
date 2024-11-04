package edu.seg2105.client.backend;

import ocsf.client.*;
import java.io.*;
import edu.seg2105.client.common.*;

/**
 * This class provides additional functionality to the client.
 * It now includes a login ID that is sent with each message.
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
  private boolean isLoggedIn = false; // Flag to track if user is already logged in
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
    isLoggedIn = true; // Set logged-in status to true after successful login
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
	        sendToServer("#quit"); // Inform server before disconnecting
	        closeConnection();
	    } catch(IOException e) {
	        System.out.println("DEBUG: IOException while quitting.");
	    }
	    System.exit(0);
	}

  /**
   * Logs off from the server.
   */
  /**
   * Logs off from the server.
   */
  public void logoff() {
      try {
          isLoggingOff = true; // Set the logoff flag
          sendToServer("#logoff"); // Send logoff message to the server
          clientUI.display("Logged off from server."); // Display message in client console
          isLoggedIn = false;  // Reset the logged-in status
          closeConnection();  // Close the connection gracefully
      } catch (IOException e) {
          clientUI.display("Error logging off.");
      } finally {
          isLoggingOff = false; // Reset the flag
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
   * Returns the current host.
   */
  public String getCurrentHost() {
    return super.getHost();
  }

  /**
   * Returns the current login ID.
   */
  public String getCurrentLoginId() {
    return this.loginId;
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
    if (message.startsWith("#login")) {
        // Check if a login attempt is made after the user is already logged in
        if (isLoggedIn) {
            clientUI.display("Error - Second login detected. You will be logged out.");
            quit(); // Terminate the client connection after the second login attempt
            return; // Do not send the second login command to the server
        }
    }

    try {
      sendToServer(message); // Send the message without the loginId prefix
    } catch(IOException e) {
      clientUI.display("Could not send message to server. Terminating client.");
      closeClient();
    }
  }

  @Override
  protected void connectionClosed() {
      if (!isLoggingOff && !isClosing) { // Only display shutdown message if not logging off or closing
          clientUI.display("The server has shut down.");
      }
      isLoggingOff = false; // Reset logoff flag
      isLoggedIn = false; // Reset login status after connection closed
      System.exit(0); // Terminate the client program
  }


  @Override
  protected void connectionException(Exception exception) {
    if (!isClosing) {
      isClosing = true;
      clientUI.display("Connection error: " + exception.getMessage());
      clientUI.display("The server has shut down unexpectedly.");
      // Do not attempt to call closeConnection here, as it may already be in process
      isClosing = false; // Reset closing flag
    }
    isLoggedIn = false; // Reset login status after connection exception
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

