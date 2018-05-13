package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import server.Response;

public class Client {
	
	public static final int DEFAULT_SERVER_PORT = 15376;
	public static final String DEFAULT_SERVER_HOST = "127.0.0.1";
	

	public static void main(String[] args) {
		
		int port = getPortNumber(args);
		String host = getHostAddress(args);
		
		Socket socket = null;
		ObjectInputStream objectInputStream = null;
		PrintWriter writer = null;
		BufferedReader console = null;
		
		try {
			
			// By default this connects to 127.0.0.1:15376
			socket = new Socket(host, port);
			System.out.println("Connected to " + host + " on port " + port);
			
			objectInputStream = new ObjectInputStream(socket.getInputStream());
			writer = new PrintWriter(socket.getOutputStream(), true);
			console = new BufferedReader(new InputStreamReader(System.in));
			
			String line;
			Response command;
			
			do {
				command = (Response) objectInputStream.readObject();
				
				
				if (command.getType() == Response.READLINE) {
					System.out.print(command.getMessage());
					line = console.readLine();
					writer.println(line);
				}
				else {
					System.out.println(command.getMessage());
				}
			}
			while(command.getType() != Response.QUIT);
			
		}
		catch (ClassNotFoundException e) {
			System.err.println("The command data sent by the server cannot be read by this client. " + e.getMessage());
		}
		catch (UnknownHostException e) {
			System.err.printf("Server %s:%d cannot be found\n", host, port);
		}
		catch (IOException e) {
			System.err.println("Sorry, something went wrong. " + e.getMessage());
		}
		finally {
			try {
				if (console != null) console.close();
				if (writer != null) writer.close();
				if (objectInputStream != null) objectInputStream.close();
				if (socket != null) socket.close();
			}
			catch (IOException e) {
				System.err.println("Sorry, something went wrong while closing the connection. " + e.getMessage());
			}
		}
		
		
		

	}
	
	/**
	 * Gets the host address from command line arguments
	 * Host address must be the first command line argument
	 * 
	 * @param args
	 * @return
	 */
	public static String getHostAddress(String[] args) {
		String address = DEFAULT_SERVER_HOST;
		
		if (args.length < 1) {
			return address;
		}
		
		address = args[0];
		return address;
	}
	
	
	/**
	 * Gets the main port number from command line arguments.<br/>
	 * The main port number must be the second command line argument
	 * 
	 * @param args
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = DEFAULT_SERVER_PORT;
		
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
}