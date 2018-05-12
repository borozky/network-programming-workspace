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

public class NonBlockingServer {
	
	public static final int SERVER_PORT = 15376;
	public static final int BUFFER_SIZE = 4096;
	
	public static void main(String[] args) throws Exception {
		
		ByteBuffer buffer = null;
		SocketAddress address = null;
		ServerSocketChannel serverSocketChannel = null;
		ServerSocket serverSocket = null;
		SocketChannel socketChannel = null;
		Selector selector = null;
		
		
		try {
			// launch server
			address = new InetSocketAddress(SERVER_PORT);
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
				
				int readyCount = selector.select();
				if (readyCount <= 0) {
					continue;
				}
				
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectionKeys.iterator();
				
				while (iterator.hasNext()) {
					SelectionKey key = iterator.next();
					iterator.remove();
					
					// setup connection
					if (key.isAcceptable()) {
						socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ);
						System.out.println("Client " + socketChannel.getRemoteAddress() + " connected");
						
						buffer = ByteBuffer.allocate(BUFFER_SIZE);
					}
					
					// READ client replies
					// do not decide to close the connection here, 
					// do that after we sent our replies to the client
					if (key.isReadable()) {
						buffer.clear();
						socketChannel.read(buffer);
						buffer.flip();
						
						byte[] bytes = new byte[buffer.limit()];
						buffer.get(bytes);
						response = new String(bytes).trim();
						System.out.println("Client: " + response);
						
						socketChannel.register(selector, SelectionKey.OP_WRITE);
						
						// do not clear the buffer, we're going to use the buffer for writing
					}
					
					// write buffer contents to the client
					// decided whether to close the connection or not
					if (key.isWritable()) {
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
					
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
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
	
	
	

}
