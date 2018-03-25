package echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;



/**
 * EchoServer client
 * <p>This program sends lines of messages to the server and receives a 
 * reply back. When server replies back an uppercase 'X' character, the 
 * client quits.
 * <p>This client also calculates and sends the resulting checksum to 
 * a separate socket connection. 
 * <p>You may provide the main(String[] args) will 3 command line 
 * arguments.
 * <p>1. Host address. Defaults to '127.0.0.1'
 * <p> 2. Main port number to connect to. Defaults to port 15376.
 * <p>3. Port number for sending checksum values. Defaults to port 
 * 25376.
 */
public class Client {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	public static final String DEFAULT_HOST = "127.0.0.1";

	public static void main(String[] args) {
		
		// get port and host from command line args
		int port = getPortNumber(args);
		int checksumPort = getPortNumberForChecksums(args);
		String address = getHostAddress(args);

		if (port == checksumPort) {
			System.err.printf("\n******ERROR: Main port and port for checksums should NOT be the same******\n\n");
			return;
		}

		
		// main socket and readers
		Socket socket = null;
		CheckedOutputStream checkedOutputStream = null;
		PrintWriter output = null;
		BufferedReader input = null;
		BufferedReader console = null;
		
		// another socket to send checksums
		Socket checksumSocket = null;
		PrintWriter checksumWriter = null;

		try {

			/**
			 * Connect to the server with 2 sockets
			 * - main socket for sending messages
			 * - another socket for sending checksums
			 */
			socket = new Socket(address, port);
			checksumSocket = new Socket(address, checksumPort);

			String localSocketAddress = socket.getLocalAddress().getHostAddress();
			int localPortNumber = socket.getLocalPort();
			System.out.printf("Client %s:%d successfully launched\n", localSocketAddress, localPortNumber);
			System.out.println("Connected to " + address + " on port " + port);
			
			
			// setup input and output streams
			checkedOutputStream = new CheckedOutputStream(socket.getOutputStream(), new CRC32());
			output = new PrintWriter(checkedOutputStream, true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			console = new BufferedReader(new InputStreamReader(System.in));
			checksumWriter = new PrintWriter(checksumSocket.getOutputStream(), true);
			
			printInstructions();
			

			/**
			 * Read a line from console. When 'Enter' is pressed, 2 things happen:
			 * - sends the message line to the server via the main socket (default port: 15376).
			 * - calculates the checksum, then sends the result to socket assigned to checksums (default port: 25376)
			 * 
			 * This also expects an immediate reply from the server. Server replies are printed.
			 * If the reply is an single uppercase 'X' this client program quits.
			 */
			String line;
			System.out.print("Input: ");
			while ((line = console.readLine()) != null) {
				// send the message to the main socket
				output.println(line);

				// calculates the checksum, 
				// send the result to assigned socket for checksums
				long checksum = checkedOutputStream.getChecksum().getValue();
				checksumWriter.println(checksum);
				System.out.println("Checksum: " + Long.toString(checksum));

				// Print server replies
				String reply = input.readLine();
				System.out.println("Reply: " + reply);
				
				// quit if reply is 'X'
				if (reply.equals("X")) {
					break;
				}
				
				System.out.print("Input: ");
			}
			
			connectionTerminated();
			
		} 
		// Bad/unknown host
		catch (UnknownHostException unknownHostEx) {
			System.err.println("ERROR: Unknown host " + address);
			unknownHostEx.printStackTrace();
		}
		// Some errors like network interruption, security problems, server not found, etc
		catch (IOException ioEx) {
			System.err.println("ERROR: Sorry! Something went wrong. " + ioEx.getMessage());
			ioEx.printStackTrace();
		}
		// close connection gracefully, 
		// main socket opened first, so close it last.
		finally {
			try {
				if (checksumWriter != null) checksumWriter.close();
				if (checksumSocket != null) checksumSocket.close();
				if (console != null) console.close();
				if (input != null) input.close();
				if (output != null) output.close();
				if (checkedOutputStream != null) checkedOutputStream.close();
				if (socket != null) socket.close();
			} catch (IOException e) {
				System.err.println("ERROR: Something went wrong while closing the connection");
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Gets the main port number from command line arguments.<br/>
	 * The main port number must be the second command line argument
	 * 
	 * @param args
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_PORT_NUMBER;
		
		if (args.length < 2) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[1]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("%s is not a valid port number", args[0]);
		}
		
		return portNumber;
	}


	/**
	 * Gets the 3rd argument from the main(String[] args).
	 * <p>The 3rd argument will be used as port for sending checksums
	 * 
	 * @param String[] Command line arguments
	 * @return int
	 */
	public static int getPortNumberForChecksums(String[] args) {
		int portNumberForChecksums = Server.CHECKSUM_PORT_NUMBER;

		if (args.length < 3) {
			return portNumberForChecksums;
		}

		try {
			portNumberForChecksums = Integer.parseInt(args[2]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("ERROR: %s is not a valid port number\n", args[2]);
		}

		return portNumberForChecksums;
	}


	
	
	/**
	 * Gets the host address from command line arguments
	 * Host address must be the first command line argument
	 * 
	 * @param args
	 * @return
	 */
	public static String getHostAddress(String[] args) {
		String address = DEFAULT_HOST;
		
		if (args.length < 1) {
			return address;
		}
		
		address = args[0];
		return address;
	}
	
	
	/**
	 * Simple callback to print instructions
	 */
	public static void printInstructions() {
		System.out.println();
		System.out.println("+----------------------------------------+");
		System.out.println("| INSTRUCTIONS:                          |");
		System.out.println("| - Type your input.                     |");
		System.out.println("| - Press enter to send                  |");
		System.out.println("| - Type 'X' or 'x' then enter to exit   |");
		System.out.println("+----------------------------------------+");
		System.out.println();
	}
	
	
	/**
	 * Simple callback to show message that connection to 
	 * server was terminated
	 */
	public static void connectionTerminated() {
		System.out.println();
		System.out.println("******* Connection terminated ********");
		System.out.println();
	}
}
