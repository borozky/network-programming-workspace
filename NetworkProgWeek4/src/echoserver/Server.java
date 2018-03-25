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
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;


/**
 * Simple server program that takes user input and throws it back 
 * to the client. This server will continue to listen unless the 
 * user stops the server manually or some error happens.
 * <p>
 * Hosts 2 socket connections:
 * <p>
 * 1. The first socket will read client's messages and reply back the 
 * received messages as upper-case text. This socket runs on port 
 * 15376 by default (can be overriden by supplying arguments to 
 * main())
 * <br/>
 * 2. The other socket will get checksums sent by the client. The 
 * server will tell if checksums sent by the client and calculated 
 * from this server matches<p>
 */
public class Server {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	public static final int CHECKSUM_PORT_NUMBER = 25376;

	public static final String LOG_FILE = "echoserver.log";
	public static Logger logger;
	
	
	/**
	 * Set up logging. New logs are appended. 
	 * If log file doesn't exist, it is automatically created.
	 */
	static {
		logger = Logger.getLogger(Server.class.getName());
		
		/**
		 * Logger prints the log to the console even if we add
		 * new log handlers. We don't want that.
		 */
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
	
	/**
	 * Main method can take 2 optional arguments
	 * <p>
	 * 1. The main port number for receiving client 
	 * messages. Defaults to port <b>15376</b>
	 * <p>
	 * 2. Port for receiving checksums calculated by the 
	 * client. Defaults to port <b>25376</b>
	 */
	public static void main(String[] args) {
		
		// get the port from args. If args is empty, use port 15376
		int port = getPortNumber(args);
		int checksumPort = getPortNumberForChecksums(args);

		if (port == checksumPort) {
			System.err.printf("\n******ERROR: Main port and port for checksums should NOT be the same******\n\n");
			return;
		}
		
		// main socket
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		CheckedInputStream checkedInputStream = null;
		PrintWriter writer = null;
		BufferedReader reader = null;

		// Separate socket for reading checksum 
		ServerSocket serverSocketForChecksums = null;
		Socket socketForChecksums = null;
		BufferedReader readerForChecksums = null;

		
		try {
			// create 2 servers, the main one and for checksums
			serverSocket = new ServerSocket(port);
			serverSocketForChecksums = new ServerSocket(checksumPort);

			waitingForConnection(port);
			
			
			// listen for connections 24/7
			do {

				// listen for main socket and socket for receiving checksums
				clientSocket = serverSocket.accept();
				socketForChecksums = serverSocketForChecksums.accept();
				clientConnected(clientSocket);

				
				// read responses
				checkedInputStream = new CheckedInputStream(clientSocket.getInputStream(), new CRC32());
				reader = new BufferedReader(new InputStreamReader(checkedInputStream));
				writer = new PrintWriter(clientSocket.getOutputStream(), true); // true means keep accepting new lines

				// read checksums
				readerForChecksums = new BufferedReader(new InputStreamReader(socketForChecksums.getInputStream()));

				
				// - Print client message, then reply back.
				// - When client presses enter, checksums will also be sent 
				//   to this server via a separate socket.
				String line;
				while ((line = reader.readLine()) != null) {

					// print and log client messages
					printClientMessage(line);
					log(line);

					// read and evaluate checksum whether they match
					String checksumFromClient = readerForChecksums.readLine();
					long calculatedChecksum = checkedInputStream.getChecksum().getValue();
					boolean isChecksumsMatch = checksumFromClient.trim().equals(Long.toString(calculatedChecksum));
					System.out.println("---calculated checksum: " + calculatedChecksum);
					System.out.println("---checksum from client: " + checksumFromClient);
					System.out.println("---checksums match? " + (isChecksumsMatch ? "yes" : "NO"));
					
					// reply back the client's message as upper-case letters
					String response = line.toUpperCase();
					writer.println(response);
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
		// close the streams and sockets
		finally {
			try {
				if (readerForChecksums != null) readerForChecksums.close();
				if (socketForChecksums != null) socketForChecksums.close();
				if (serverSocketForChecksums != null) serverSocketForChecksums.close();
				if (reader != null) reader.close();
				if (writer != null) writer.close();
				if (checkedInputStream != null) checkedInputStream.close(); 
				if (clientSocket != null) clientSocket.close();
				if (serverSocket != null) serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Simple callback for printing client's messages
	 * @param String Client message
	 */
	public static void printClientMessage(String line) {
		System.out.println("Client: " + line);
	}


	/**
	 * Get the first port number provided by command line arguments 
	 * (main(String[] args))
	 * <p>
	 * The first port number will be used to receive client messages
	 * 
	 * @param args If there are no args provided (ie. empty array), 
	 * 			   it will use the default port 15376
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_PORT_NUMBER;
		
		if (args.length < 1) {
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
	 * Get the 2 port number from command line arguments (main(String[] args))
	 * <p>
	 * The second port number will be used to get checksums calculated by the 
	 * client
	 * 
	 * @param args If number of args is less than 2, it will use port 25376
	 * @return int
	 */
	public static int getPortNumberForChecksums(String[] args) {
		int portNumberForChecksums = CHECKSUM_PORT_NUMBER;

		if (args.length < 2) {
			return portNumberForChecksums;
		}

		try {
			portNumberForChecksums = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("ERROR: %s is not a valid port number\n", args[1]);
		}

		return portNumberForChecksums;
	}
	
	
	/**
	 * Simple callback saying the client is connected
	 * 
	 * @param Socket
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
