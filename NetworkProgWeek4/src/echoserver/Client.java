package echoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;



/**
 * Connected to 127.0.0.1 on port 15376
 * 
 * +----------------------------------+
 * | INSTRUCTIONS:                    |
 * | - Type your input.               |
 * | - Press enter to send            |
 * | - Type 'X' then enter to exit    |
 * +----------------------------------+
 * 
 * Input: Hello
 * Reply: Hello
 * Input: World
 * Reply: World
 * Input: X
 * Reply: X
 * 
 * ******* Connection terminated ********
 */
public class Client {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	public static final String DEFAULT_HOST = "127.0.0.1";

	public static void main(String[] args) {
		
		int port = getPortNumber(args);
		String address = getHostAddress(args);
		
		Socket socket = null;
		PrintWriter output = null;
		BufferedReader input = null;
		BufferedReader consoleInput = null;
		
		try {
			socket = new Socket(address, port);
			System.out.println("Connected to " + address + " on port " + port);
			
			
			output = new PrintWriter(socket.getOutputStream(), true);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			consoleInput = new BufferedReader(new InputStreamReader(System.in));
			
			printInstructions();
			
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
			
		} catch (UnknownHostException unknownHostEx) {
			System.err.println("ERROR: Unknown host " + address);
			unknownHostEx.printStackTrace();
		}
		catch (IOException ioEx) {
			System.err.println("ERROR: Sorry! Server hasn't started yet or something went wrong");
			ioEx.printStackTrace();
		} finally {
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
	
	
	public static String getHostAddress(String[] args) {
		String address = DEFAULT_HOST;
		
		if (args.length < 1) {
			return address;
		}
		
		address = args[0];
		return address;
	}
	
	
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
	
	public static void connectionTerminated() {
		System.out.println();
		System.out.println("******* Connection terminated ********");
		System.out.println();
	}
}
