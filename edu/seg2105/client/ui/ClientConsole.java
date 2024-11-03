package edu.seg2105.client.ui;

import java.io.*;
import java.util.Scanner;
import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

public class ClientConsole implements ChatIF 
{
  final public static int DEFAULT_PORT = 5555;
  
  ChatClient client;
  Scanner fromConsole; 

  public ClientConsole(String host, int port) 
  {
    try 
    {
      client = new ChatClient(host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't set up connection! Terminating client.");
      System.exit(1);
    }
    
    fromConsole = new Scanner(System.in); 
  }

  public void accept() 
  {
    try
    {
      String message;
      while (true) 
      {
        message = fromConsole.nextLine();

        // Check if the input is a command
        if (message.startsWith("#")) 
        {
          handleCommand(message);
        } 
        else 
        {
          // Send message to server
          client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println("Unexpected error while reading from console!");
    }
  }

  private void handleCommand(String message) 
  {
    switch (message) 
    {
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
        } 
        else if (message.startsWith("#setport ")) {
          try {
            int port = Integer.parseInt(message.substring(9));
            client.updatePort(port);
          } catch (NumberFormatException e) {
            System.out.println("Invalid port number.");
          }
        } 
        else {
          System.out.println("Unknown command.");
        }
    }
  }

  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  public static void main(String[] args) 
  {
    String host = "";
    int port = DEFAULT_PORT;

    try 
    {
      host = args[0];
      if (args.length > 1) 
      {
        port = Integer.parseInt(args[1]);
      }
    } 
    catch (Exception e) 
    {
      host = "localhost";
    }

    ClientConsole chat = new ClientConsole(host, port);
    chat.accept();
  }
}

