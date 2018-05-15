package task2.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Program that accepts and sends back inputs from the client. 
 * Server closes when client quits or when it receives a single 'x'.
 * <p>You can pass 1 optional argument<ul>
 * <li>The first argument is the port number. Defaults to port 15376</li></ul>
 */
public class NonBlockingServer {
	
	public static final int SERVER_PORT = 15376;
	public static final int BUFFER_SIZE = 4096;
	
	public static void main(String[] args) throws Exception {

		int port = getPortNumber(args);
		
		ByteBuffer buffer = null;
		SocketAddress address = null;
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		SocketChannel socketChannel = null;
		Selector selector = null;
		
		
		try {
			// launch server
			address = new InetSocketAddress(port);
			serverSocketChannel = ServerSocketChannel.open();
			serverSocket = serverSocketChannel.socket();
			serverSocket.bind(address);
			System.out.println("Server started on port " + serverSocket.getLocalPort());
			
			// non-blocking
			serverSocketChannel.configureBlocking(false);
			
			// register server socket channel selector
			// the server socket channel will listen for ACCEPT events only
			// Optionally you can use "ssc.register(selector, ssc.validOps())" as alternative
			selector = Selector.open();
			serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Waiting for connections...");
			
			String response = "";
			
			while (true) {
				
				// select() will block until an operation (eg. ACCEPT, READ, WRITE) is ready
				// readyCount may return 0, so let's not process when nothing is ready
				int readyCount = selector.select();
				if (readyCount <= 0) {
					continue;
				}

				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();


				while (iterator.hasNext()) {

					try {
						// Get key
						SelectionKey key = iterator.next();

						// remove recent key to prevent it from being reprocessed again
						// in the next iteration
						iterator.remove();
						
						// setup connection
						if (key.isValid() && key.isAcceptable()) {
							try {
								socketChannel = serverSocketChannel.accept();
								socketChannel.configureBlocking(false);
								socketChannel.register(selector, SelectionKey.OP_READ);
								System.out.println("Client " + socketChannel.getRemoteAddress() + " connected");
								
								buffer = ByteBuffer.allocate(BUFFER_SIZE);
							} catch (IOException e) {
								System.err.println("Sorry something went wrong. " + e.getMessage());
								if (socketChannel != null) socketChannel.close();
							}
							
						}
						
						// READ client replies
						// do not decide to close the connection here, 
						// do that after we sent our replies to the client, or some error happens
						if (key.isValid() && key.isReadable()) {
							
							try {
								buffer.clear();
								
								// read from client. May return 0 or -1
								int c = socketChannel.read(buffer);
								if (c > 0) {
									buffer.flip();
									byte[] bytes = new byte[buffer.limit()];
									buffer.get(bytes);
									response = new String(bytes).trim();
									System.out.println("Client: " + response);
									socketChannel.register(selector, SelectionKey.OP_WRITE);
								}
								// End of stream. In that case, close the connection
								else if (c == -1) {
									System.err.println("Client closed the connection.");
									if (socketChannel != null) socketChannel.close();
								}
							
							} catch (IOException e) {
								System.err.println("Sorry something went wrong. " + e.getMessage());
								if (socketChannel != null) socketChannel.close();
							}
							
							
							// do not clear the buffer, we're going to use the buffer for writing
						}
						
						// write buffer contents to the client
						// decided whether to close the connection or not
						if (key.isValid() && key.isWritable()) {
							
							// writing may create problems, if that's the case, close the connection
							try {
								buffer.flip();
								socketChannel.write(buffer);
								
								if (response.equals("x")) {
									// do not register an OP_ACCEPT here, or else it will throw ClosedChannelException here
									// just close the socket and the selector will take care of the rest
									String addr = socketChannel.getRemoteAddress().toString();
									socketChannel.close();
									System.out.println("Connection to client " + addr + " closed successfully.");
								} else {
									// we're expecting the client to send a reply for us to READ
									socketChannel.register(selector, SelectionKey.OP_READ);
								}
							}
							catch (IOException e) {
								System.err.println("Sorry something went wrong. " + e.getMessage());
								if (socketChannel != null) socketChannel.close();
							}

						}
					}
					// Client disconnected for some reason
					catch (IOException e) {
						System.err.println("Sorry something went wrong. " + e.getMessage());
						if (socketChannel != null) socketChannel.close();
					}
					
				} // end while
				
			}
		}
		catch (IOException e) {
			System.out.println("Sorry. Something went wrong. " + e.getMessage());
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
	
	
	

}
