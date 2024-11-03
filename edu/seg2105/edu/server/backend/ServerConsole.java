package edu.seg2105.edu.server.backend;
import edu.seg2105.client.common.ChatIF;
import java.util.Scanner;

/**
 * This class provides the console interface for a server.
 */
public class ServerConsole implements ChatIF, Runnable {
  
  private EchoServer server; // The server instance
  private Scanner fromConsole; // Scanner for user input

  /**
   * Constructs an instance of the ServerConsole UI.
   *
   * @param server The instance of the EchoServer
   */
  public ServerConsole(EchoServer server) {
    this.server = server;
    fromConsole = new Scanner(System.in);
  }

  /**
   * This method waits for input from the console. Once it is 
   * received, it sends it to the server's message handler.
   */
  public void accept() {
	    try {
	        String message;
	        while (true) {
	            message = fromConsole.nextLine();
	            server.handleMessageFromServerConsole(message); // Pass the message directly
	        }
	    } catch (Exception ex) {
	        System.out.println("Unexpected error while reading from console!");
	    }
	}
  /**
   * Display method required by the ChatIF interface.
   * Displays a message onto the server console.
   *
   * @param message The string to be displayed.
   */
  @Override
  public void display(String message) {
    System.out.println(message);
  }

  /**
   * Run method to start the console input in a separate thread.
   */
  @Override
  public void run() {
    accept();
  }
}
