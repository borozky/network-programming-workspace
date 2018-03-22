package echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

/**
 * EchoServer started. 
 * Listening on port 15376...
 * 
 * +-----------------------------+
 * |       Client connected      |
 * +-----------------------------+
 * Client: Hello
 * Client: World
 * Client: X
 * 
 * ******** CLIENT DISCONNECTED *********
 * 
 * Listening on port 15376...
 * 
 * +-----------------------------+
 * |       Client connected      |
 * +-----------------------------+
 * Client: Hello
 * Client: World
 * Client: X
 * 
 * ******** CLIENT DISCONNECTED *********
 * 
 */
public class Server {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	
	
	public static void main(String[] args) {
		int port = getPortNumber(args);
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader input = null;
		
		try {
			// create server
			serverSocket = new ServerSocket(port);
			waitingForConnection(port);
			
			do {
				// accept client
				clientSocket = serverSocket.accept();
				clientConnected();
				
				// accept input
				out = new PrintWriter(clientSocket.getOutputStream(), true); // true: auto-flush
				input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				// print response
				String line;
				while ((line = input.readLine()) != null) {
					System.out.println("Client: " + line);
					out.println(line);
				}
				
				clientDisconnected();
				waitingForConnection(port);
				
			} while (true);
			
			
			
		} catch (IllegalArgumentException argEx) {
			System.err.println("Invalid port number");
		} catch (IOException ioEx) {
			System.err.println("Sorry! Something went wrong.");
			ioEx.printStackTrace();
		} finally {
			try {
				if (input != null) input.close();
				if (out != null) out.close();
				if (clientSocket != null) clientSocket.close();
				if (serverSocket != null) serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_PORT_NUMBER;
		
		if (args.length == 0) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("ERROR: %s is not a valid port number\n", args[0]);
		}
		
		return portNumber;
	}
	
	
	public static void clientConnected() {
		System.out.println();
		System.out.println("+-----------------------------+");
		System.out.println("|       Client connected      |");
		System.out.println("+-----------------------------+");
		System.out.println("Waiting for input...");
		
	}
	
	public static void clientDisconnected() {
		System.out.println();
		System.out.println("******** CLIENT DISCONNECTED *********");
		System.out.println();
	}
	
	public static void waitingForConnection(int port) {
		System.out.println("Listening on port " + port + "...");
	}

}
