// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

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
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable. It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  /**
   * Flag to prevent recursive calls during shutdown.
   */
  private boolean isClosing = false;

  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); // Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
    System.out.println("DEBUG: Client successfully connected to server on port " + port);
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display("Could not send message to server. Terminating client.");
      System.out.println("DEBUG: IOException in handleMessageFromClientUI - calling closeClient()");
      closeClient(); // Directly close the client if sending fails
    }
  }
  
  /**
   * This method is called when the connection is closed by the server.
   */
  @Override
  protected void connectionClosed() {
    if (!isClosing) { // Ensure this only runs once
      isClosing = true;
      System.out.println("DEBUG: connectionClosed() called - Server has shut down gracefully.");
      clientUI.display("The server has shut down.");
      closeClient();
    }
  }

  /**
   * This method is called when an exception occurs in the connection.
   *
   * @param exception The exception thrown.
   */
  @Override
  protected void connectionException(Exception exception) {
    if (!isClosing) { // Ensure this only runs once
      isClosing = true;
      System.out.println("DEBUG: connectionException() called - Server shut down unexpectedly.");
      clientUI.display("Connection error: " + exception.getMessage());
      clientUI.display("The server has shut down unexpectedly.");
      closeClient();
    }
  }

  /**
   * This method safely terminates the client without recursion.
   */
  private void closeClient() {
    System.out.println("DEBUG: Entering closeClient()");
    try {
      closeConnection();
      System.out.println("DEBUG: closeConnection() called successfully in closeClient()");
    } catch (IOException e) {
      System.out.println("DEBUG: IOException in closeClient() - likely connection already closed.");
    }
    System.out.println("DEBUG: Exiting application in closeClient()");
    System.exit(0); // Directly exit the application
  }
}
// End of ChatClient class

