package edu.seg2105.edu.server.backend;
import edu.seg2105.client.common.ChatIF;
import java.util.Scanner;


/**
 * This class provides the console interface for a server.
 * It listens for server commands and forwards them to the EchoServer instance.
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
   * This method waits for input from the console. If the input is a command,
   * it executes the appropriate server function. Otherwise, it sends it to the
   * server's message handler.
   */
  public void accept() {
    try {
      String message;
      while (true) {
        message = fromConsole.nextLine();
        if (message.startsWith("#")) {
          handleCommand(message);
        } else {
          server.handleMessageFromServerConsole(message);
        }
      }
    } catch (Exception ex) {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  /**
   * Handles server commands from the console.
   *
   * @param command The command entered by the server user.
   */
  private void handleCommand(String command) {
    if (command.equalsIgnoreCase("#quit")) {
      server.quit();
    } else if (command.equalsIgnoreCase("#stop")) {
      server.stopListeningForClients();
    } else if (command.equalsIgnoreCase("#close")) {
      server.closeServer();
    } else if (command.startsWith("#setport")) {
      try {
        int port = Integer.parseInt(command.split(" ")[1]);
        server.setCustomPort(port); // Use setCustomPort if setPort is final in EchoServer
      } catch (Exception e) {
        System.out.println("Invalid port number.");
      }
    } else if (command.equalsIgnoreCase("#start")) {
      server.startServer();
    } else if (command.equalsIgnoreCase("#getport")) {
      server.displayCurrentPort();
    } else {
      System.out.println("Unknown command.");
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
// End of ServerConsole class

