package task1.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Arrays;

/**
 * <p>Program that listens for clients and sends back any receive client 
 * replies. This server will disconnect from the client when it receives a 'x' 
 * from the client
 * <p> You can pass the following 1 optional argument into the command line.
 * <ul><li>Port number that this server will listen on. Will use port 
 * 15376</li></ul>
 * 
 * @author user
 *
 */
public class Server {
	
	public static final int SERVER_PORT = 15376;
	public static final int BUFFER_SIZE = 4096;
	

	public static void main(String[] args) throws IOException, Exception {
		
		ByteBuffer buffer = null;
		SocketAddress address = null;
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		SocketChannel socketChannel = null;
		
		try {
			// launch server
			address = new InetSocketAddress(SERVER_PORT);
			serverSocketChannel = ServerSocketChannel.open();
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(address);
			
			// use blocking
			serverSocketChannel.configureBlocking(true);
			
			// save client response here
			String response = "";
			
			
			do {
				// listen for a client connection, 
				// Note: accept() will block
				System.out.println("Waiting for connection...");
				socketChannel = serverSocketChannel.accept();
				System.out.println("Incoming connection from " + socketChannel.socket().getRemoteSocketAddress());
				
				// allocate new buffer for every connection made
				buffer = ByteBuffer.allocate(BUFFER_SIZE);
				
				try {
					
					// sends some welcome message
					buffer.rewind();
					buffer.put("Welcome, client!!!".getBytes());
					buffer.flip();
					socketChannel.write(buffer);
					buffer.clear();
					
					// continue accepting input until client sends an "x"
					do {
						// guarantees we read from position 0
						buffer.rewind();
						
						// fill the buffer with submitted data
						// Note: read() will modify position depending on num of bytes received
						// eg. when it received 10 bytes of data, position will be 10
						socketChannel.read(buffer);
						
						// previous operation modifies the position, 
						// so use flip to work on data again
						// eg. if prev operation receives 10 bytes, when doing flip changes buffer limit to 10
						buffer.flip();
						
						
						// sends n bytes of the buffer to client
						// n limit is set when we did the flip on previous line (eg: limit is 10 previously)
						socketChannel.write(buffer);
						
						
						// prev operation modifies the position
						// we're going to read data again byte by byte so flip
						buffer.flip();
						
						
						// populate 'bytes' variable with contents from the buffer 
						// by doing so buffer position will change
						byte[] bytes = new byte[buffer.limit()];
						buffer.get(bytes);
						
						
						// convert byte to string
						response = new String(bytes).trim();
						System.out.println("Client: " + response);
						
						
						// set to position back to 0 for next data 
						// Note: old data is not destroyed
						buffer.clear();
					}
					while (response.equals("x") == false);
					
					
				}	
				// Client disconnected for some reason
				catch (IOException e) {
					System.err.println("Sorry. client has forcibly closed the connection. Message: " + e.getMessage());
				}
				finally {
					try {
						if (socketChannel != null) socketChannel.close();
					} catch (IOException e) {
						
						// Kill the server when closing connection fails
						throw new Exception("Something went wrong while closing the connection. " + e.getMessage());
					}
				}
				
				// listen for connections again
			} while(true);
			
		}
		catch (IOException e) {
			System.err.println("Sorry, something went wrong. " + e.getMessage());
		} 
		finally {
			try {
				if (socketChannel != null) socketChannel.close();
				if (serverSocket != null) serverSocket.close();
				if (serverSocketChannel != null) serverSocketChannel.close();
			} catch (IOException e) {
				System.err.println("Sorry, something went wrong while closing the connection. " + e.getMessage());
			}
		}
		

	}
	
	/**
	 * Gets the main port number from command line arguments.<br/>
	 * The main port number must be the first command line argument
	 * 
	 * @param args
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = SERVER_PORT;
		
		if (args.length < 1) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("%s is not a valid port number", args[0]);
		}
		
		return portNumber;
	}
	
	
	// Display bytebuffer's position, limit, and all contents
	// NOTE: Before using this method, change the BUFFER_SIZE back to a small number (eg. 16)
	public static void debug(ByteBuffer buffer) {
		System.out.printf("Position: %d, Limit: %d\n", buffer.position(), buffer.limit());
		System.out.println(Arrays.toString(buffer.array()));
		System.out.println("Buffer contents: " + new String(buffer.array()));
		System.out.println();
	}

}
