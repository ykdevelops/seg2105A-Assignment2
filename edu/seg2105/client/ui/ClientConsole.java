package edu.seg2105.client.ui;

import java.io.*;
import java.util.Scanner;
import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class provides the console interface for a client.
 * It takes in a mandatory login ID, an optional host, and an optional port.
 */
public class ClientConsole implements ChatIF {
  
  // Default port if none is provided
  final public static int DEFAULT_PORT = 5555;
  
  private ChatClient client; // The chat client instance
  private Scanner fromConsole; // Scanner for user input

  /**
   * Constructs an instance of the ClientConsole UI.
   * 
   * @param loginId The login ID for the client (required).
   * @param host The server host (default is "localhost").
   * @param port The server port (default is 5555).
   */
  public ClientConsole(String loginId, String host, int port) {
    try {
      client = new ChatClient(loginId, host, port, this);
    } catch (IOException exception) {
      System.out.println("Error: Can't set up connection! Terminating client.");
      System.exit(1);
    }
    
    fromConsole = new Scanner(System.in); 
  }

  /**
   * Waits for console input from the user and sends it to the server.
   * Commands are handled separately.
   */
  public void accept() {
    try {
      String message;
      while (true) {
        message = fromConsole.nextLine();

        // Check if the input is a command
        if (message.startsWith("#")) {
          handleCommand(message);
        } else {
          // Send message to server with login ID prefix
          client.handleMessageFromClientUI(message);
        }
      }
    } catch (Exception ex) {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  /**
   * Handles commands that start with "#".
   * 
   * @param message The command input by the user.
   */
  private void handleCommand(String message) {
    switch (message) {
      case "#quit":
        client.quit();
        break;
        
      case "#logoff":
        client.logoff();
        break;
        
      case "#login":
        client.login();
        break;
        
      case "#gethost":
        System.out.println("Current host: " + client.getCurrentHost());
        break;
        
      case "#getport":
        System.out.println("Current port: " + client.getCurrentPort());
        break;
        
      default:
        if (message.startsWith("#sethost ")) {
          String host = message.substring(9);
          client.updateHost(host);
        } else if (message.startsWith("#setport ")) {
          try {
            int port = Integer.parseInt(message.substring(9));
            client.updatePort(port);
          } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
          }
        } else {
          System.out.println("Unknown command.");
        }
    }
  }

  /**
   * Displays a message onto the console.
   * 
   * @param message The string to be displayed.
   */
  public void display(String message) {
    System.out.println("> " + message);
  }

  /**
   * Main method to run the ClientConsole program.
   * 
   * @param args Command line arguments. The first argument should be the login ID,
   *             followed optionally by the host and port.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("ERROR - No login ID specified. Connection aborted.");
      System.exit(1);
    }
    
    String loginId = args[0];
    String host = args.length > 1 ? args[1] : "localhost";
    int port = args.length > 2 ? Integer.parseInt(args[2]) : DEFAULT_PORT;

    ClientConsole chat = new ClientConsole(loginId, host, port);
    chat.accept();
  }
}
