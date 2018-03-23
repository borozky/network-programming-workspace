package echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



/**
 * Simple program that send lines of messages to a socket server<br/><br/>
 * On startup, you may provide these 2 optional arguments:<br/>
 * - <b>Host name</b>. Defaults to '127.0.0.1'<br/>
 * - <b>Port number</b>. Defaults to port 15376<br/>
 */
public class Client {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	public static final String DEFAULT_HOST = "127.0.0.1";

	public static void main(String[] args) {
		
		// get port and host from command line args
		int port = getPortNumber(args);
		String address = getHostAddress(args);
		
		Socket socket = null;
		PrintWriter output = null;
		BufferedReader input = null;
		BufferedReader consoleInput = null;
		
		try {
			// connect to server
			socket = new Socket(address, port);
			String localSocketAddress = socket.getLocalAddress().getHostAddress();
			int localPortNumber = socket.getLocalPort();
			System.out.printf("Client %s:%d successfully launched\n", localSocketAddress, localPortNumber);
			System.out.println("Connected to " + address + " on port " + port);
			
			
			// setup input and output streams
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			consoleInput = new BufferedReader(new InputStreamReader(System.in));
			
			
			printInstructions();
			
			
			// Read line from console. On enter pressed, this client sends the message to the server
			// This client expects replies from server. 
			// When this client receives an 'X' character, the program quits
			String line;
			System.out.print("Input: ");
			while ((line = consoleInput.readLine()) != null) {
				output.println(line);
				
				String reply = input.readLine();
				System.out.println("Reply: " + reply);
				
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
		// Some errors like network interruption, security problems, etc
		catch (IOException ioEx) {
			System.err.println("ERROR: Sorry! Server hasn't started yet or something went wrong");
			ioEx.printStackTrace();
		}
		// close connection gracefully, 
		// socket connection opened first, so close it last.
		finally {
			try {
				if (consoleInput != null) consoleInput.close();
				if (input != null) input.close();
				if (output != null) output.close();
				if (socket != null) socket.close();
			} catch (IOException e) {
				System.err.println("ERROR: Something went wrong while closing the connection");
				e.printStackTrace();
			}
		}
	}

	
	/**
	 * Gets port number from command line arguments.<br/>
	 * Port number must be the second command line argument
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
		System.out.println("+----------------------------------+");
		System.out.println("| INSTRUCTIONS:                    |");
		System.out.println("| - Type your input.               |");
		System.out.println("| - Press enter to send            |");
		System.out.println("| - Type 'X' then enter to exit    |");
		System.out.println("+----------------------------------+");
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
