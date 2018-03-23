package echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/**
 * Simple server program that takes user input and throws 
 * it back to the client<br/><br/>
 * Launches a socket server at port 15376 by default.<br/>
 * Server will continue to listen unless the user 
 * stops the server manually or some error happens
 */
public class Server {
	
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	public static final String LOG_FILE = "echoserver.log";
	public static Logger logger;
	
	
	/**
	 * Set up logging. New logs are appended. 
	 * If log file doesn't exist, it is automatically created
	 */
	static {
		logger = Logger.getLogger(Server.class.getName());
		
		// Disable default console logging
		logger.setUseParentHandlers(false);
		
		FileHandler fileHandler = null;
		
		try {
			// handle logging through files. 'true' means append to existing log file
			fileHandler = new FileHandler(LOG_FILE, true);
			fileHandler.setLevel(Level.INFO);
			
			// use SimpleFormatter instead of default XMLFormatter
			fileHandler.setFormatter(new SimpleFormatter()); 
			
			logger.addHandler(fileHandler);
		} 
		catch (SecurityException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
	}
	
	
	public static void main(String[] args) {
		
		// get the port from args. If args is empty, use port 15376
		int port = getPortNumber(args);
		
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		PrintWriter out = null;
		BufferedReader input = null;
		
		try {
			// create server
			serverSocket = new ServerSocket(port);
			waitingForConnection(port);
			
			
			// listen for connections 24/7
			do {
				// accept client
				clientSocket = serverSocket.accept();
				clientConnected(clientSocket);
				
				// accept input
				out = new PrintWriter(clientSocket.getOutputStream(), true); // true: auto-flush
				input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
				// Print client message, then reply back
				String line;
				while ((line = input.readLine()) != null) {
					
					// log message
					printClientMessage(line);
					log(line);
					
					// reply back the client's message as upper-case letters
					String response = line.toUpperCase();
					out.println(response);
				}
				
				clientDisconnected();
				waitingForConnection(port);
				
			} while (true);
			
			
			
		} 
		// Invalid port number
		catch (IllegalArgumentException argEx) {
			System.err.println("Invalid port number");
		}
		// Other exceptions like network interruption, security errors, etc
		catch (IOException ioEx) {
			System.err.println("Sorry! Something went wrong.");
			ioEx.printStackTrace();
		} 
		finally {
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
	
	
	public static void printClientMessage(String line) {
		System.out.println("Client: " + line);
	}


	/**
	 * Get the port number provided by command line arguments
	 * 
	 * @param args
	 * @return int
	 */
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
	
	
	/**
	 * Simple callback saying the client is connected
	 */
	public static void clientConnected(Socket socket) {
		String peerHostAddress = "[unknown peer address]";
		InetAddress inetAddress = socket.getInetAddress();
		if (inetAddress != null) {
			peerHostAddress = inetAddress.getHostAddress();
		}
		System.out.printf("********CLIENT %s CONNECTED *********\n", peerHostAddress);
		System.out.println("Waiting for input...");
	}
	
	
	/**
	 * Simple callback saying client is disconnected
	 */
	public static void clientDisconnected() {
		System.out.println();
		System.out.println("******** CLIENT DISCONNECTED *********");
		System.out.println();
	}
	
	
	/**
	 * Simple callback saying the server is waiting for 
	 * connection at this port
	 * 
	 * @param port
	 */
	public static void waitingForConnection(int port) {
		System.out.println("Listening on port " + port + "...");
	}
	
	
	/**
	 * Log message to the a file
	 * 
	 * @param message
	 */
	public static void log(String message) {
		logger.info(message);
	}

}
