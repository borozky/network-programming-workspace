package task1.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * <p>Program that sends user input and receives replies. This program exits
 * when the server replies a single 'x' character
 * <p>This uses NIO's buffers, channels and selectors to perform its task.
 * <p>You can pass the following 2 optional arguments into the command line
 * <ul>
 * <li>The first argument is the host address. It will connect to the 
 * host's loopback address (usually <b>127.0.0.1</b>) by default</li>
 * <li>The next argument is the port number. Will use port <b>15376</b> by 
 * default</li>
 * </ul>
 */
public class Client {
	
	public static Scanner scanner = new Scanner(System.in);
	
	// 127.0.0.1
	public static final String DEFAULT_SERVER_HOST = InetAddress.getLoopbackAddress().getHostAddress();
	public static final int DEFAULT_SERVER_PORT = 15376;
	public static final int  BUFFER_SIZE = 2048;
	
	
	/**
	 * You can pass 2 optional arguments to override the defaults<ul>
	 * <li>the first is the host address. Will use the default loopback address of 127.0.0.1</li>
	 * <li>the second is the port number. Will use 15376 by default</li>
	 */
	public static void main(String[] args) {

		int port = getPortNumber(args);
		String host = getHostAddress(args);
		
		ByteBuffer buffer = null;
		SocketAddress address = null;
		SocketChannel channel = null;
		Selector selector = null;
		
		
		try {
			// buffer is 2 kilobytes
			buffer = ByteBuffer.allocate(BUFFER_SIZE);
			
			// connect as non-blocking
			address = new InetSocketAddress(host, port);
			channel = SocketChannel.open(address);
			channel.configureBlocking(false);
			
			// create selector and listen for READ and WRITE events
			// (you can actually modify this to allow READ only, or WRITE only or include more operations)
			selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			
			String reply = "";
			
			do {
				// listen for any ready I/O operations (will block)
				// may create false positives, so check
				int readyKeys = selector.select();
				if (readyKeys == 0) {
					continue;
				}
				
				// there may be 1 or more selection keys when the code above returns
				Set<SelectionKey> selectedKeys = selector.selectedKeys();
				Iterator<SelectionKey> iterator = selectedKeys.iterator();
				
				while (iterator.hasNext()) {

					// get the key
					SelectionKey key = iterator.next();
					
					// key is ready for READ operation
					if (key.isReadable()) {
						reply = read(channel, buffer).trim();
						System.out.println("Reply: " + reply);
					}

					// when server replies an 'x', Don't give the chance to read from input again
					// and break out of the loop immediately
					if (reply.equals("x")) {
						break;
					} else {

						// when the reply is non 'x', we will send some data on the next iteration
						channel.register(selector, SelectionKey.OP_WRITE);
					}
					
					// scan from console and send the input to the server
					if (key.isWritable()) {
						write(channel, buffer);

						// after we send a reply to the server, we expect server to reply back
						channel.register(selector, SelectionKey.OP_READ);
					}

					// IMPORTANT. Remove the selection key or else 
					// the code key.isReadable() or key.isWritable() or both will return true 
					// and the code inside will unnecessesarily execute again in next iteration 
					// when only none or 1 of them is allowed to execute
					iterator.remove();
				}
			} 
			// do not use "while (true)" here, because the code "reply.equals("x")" inside will break the inner loop only
			// so check for "x" once again
			while ( ! reply.equals("x"));
			
		}
		// Server has not started
		catch (UnresolvedAddressException e) {
			System.err.println("Server cannot be found. " + e.getMessage());
		}
		// Port is invalid
		catch (IllegalArgumentException e) {
			System.err.println("Invalid address. " + e.getMessage());
		}
		// In case I forgot so set the channel to "non-blocking" mode
		// To set as "non-blocking" channel use "channel.configureBlocking(false)"
		catch(IllegalBlockingModeException e) {
			System.out.println("The connection should be in non-blocking mode. " + e.getMessage());
		}
		// Other IO exceptions like the 
		// - server crash or 
		// - someone cut off the network connection
		catch (IOException e) {
			System.err.println("Sorry, something went wrong. " + e.getMessage());
		}
		// Close the connection
		finally {
			try {
				if (selector != null) selector.close();
				if (channel != null) channel.close();
				System.out.println("Connection closed.");

			} catch (IOException e) {
				System.out.println("Sorry, something went wrong while closing the connection. " + e.getMessage());
			}
		}
	}
	
	/**
	 * Read server reply
	 * 
	 * @param channel
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public static String read(SocketChannel channel, ByteBuffer buffer) throws IOException {
		// use a string builder in case the server sends lots of data
		StringBuilder sb = new StringBuilder();
		
		// reset buffer back from start in case the buffer that is passed in the arguments is not cleared
		buffer.clear();
		
		// start reading
		int count = channel.read(buffer);
		
		// check if there are more bytes to be processed
		// For short messages, usually the count returns '0' on 2nd iteration
		while (count > 0) {
			
			// we will process the bytes filled up by channel.read()
			// use flip() so that bytes that are not filled up won't be processed
			buffer.flip();
			
			// get the string contents
			byte[] bytes = new byte[buffer.limit()];
			buffer.get(bytes);
			String append = new String(bytes);
			
			// append to string builder
			sb.append(append);
			
			// recycle the buffer for next iteration
			buffer.clear();
			
			// read again in case this client has received more data
			count = channel.read(buffer);
		}
		
		return sb.toString();
	}
	
	
	/**
	 * Reads and sends user input to the server
	 * 
	 * @param channel
	 * @param buffer
	 * @throws IOException
	 */
	public static void write(SocketChannel channel, ByteBuffer buffer) throws IOException {
		// read input
		System.out.print("Enter your input: ");

		String line = scanner.nextLine();

		// this guarantees that input sent to the server is never empty.
		// for some reason, when client sends a empty input 
		// and in turn server sends an empty reply, the selector.select() gets stuck 
		if (line.isEmpty()) {
			line = "\n";
		}
		
		// reset buffer back to original.
		// Highly likely that this buffer will have some left-over data
		buffer.clear();
		
		// transfer line contents into the buffer
		// Position will be updated on the process
		buffer.put(line.getBytes());
		
		// all space in the buffer is not necessarily filled up,
		// we're going to  process only the transferred data, so use flip.
		// Position will reset to 0
		buffer.flip();
		
		// sends the buffer's contents to the server
		// position will be incremented
		channel.write(buffer);

		buffer.clear();
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	public static final int SERVER_PORT = 15376;
//	public static final String SERVER_HOST = "127.0.0.1";
//	public static final int BUFFER_SIZE = 4096;
//
//	public static void main(String[] args) throws IOException, InterruptedException {
//		
//		ByteBuffer buffer = ByteBuffer.allocate(64);
//
//		SocketChannel channel = SocketChannel.open(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
//		channel.configureBlocking(false);
//		
//		Selector selector = Selector.open();
//		// the channel will do read and write operations
//		channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
//		
//		Scanner scanner = new Scanner(System.in);
//
//		while (true) {
//
//			int readyChannels = selector.select();
//			System.out.println("KEYS WITH READY CHANNELS: " + readyChannels);
//
//			Set<SelectionKey> selectionKeys = selector.selectedKeys();
//			Iterator<SelectionKey> iterator = selectionKeys.iterator();
//
//			while (iterator.hasNext()) {
//
//				// get the selected key
//				SelectionKey key = iterator.next();
//				iterator.remove();
//
//				if (key.isReadable()) {
//					read(key, buffer);
//				}
//
//				if (key.isWritable()) {
//					write(key, buffer, scanner);
//				}
//			}
//		}
//		
//
//
//	}
//
//	public static void read(SelectionKey key, ByteBuffer buffer) throws IOException {
//		SocketChannel channel = (SocketChannel) key.channel();
//		String reply = "";
//		buffer.clear();
//		int count = 0;
//		while ((count = channel.read(buffer)) > 0) {
//			buffer.flip();
//			byte[] bytes = new byte[buffer.limit()];
//			buffer.get(bytes);
//			reply = new String(bytes);
//			buffer.clear();
//		}
//
//		System.out.println("REPLY:" + reply);
//	}
//
//	public static void write(SelectionKey key, ByteBuffer buffer, Scanner scanner) throws IOException {
//		SocketChannel channel = (SocketChannel) key.channel();
//		String line = "";
//		System.out.print("Enter your input: ");
//		line = scanner.nextLine();
//		buffer.clear();
//		buffer.put(line.getBytes());
//		buffer.flip();
//		channel.write(buffer);
//		buffer.clear();
//	}

	// for debugging purposes
	public static void debug(ByteBuffer buffer) {
		System.out.printf("Position: %d, Limit: %d\n", buffer.position(), buffer.limit());
		System.out.println(Arrays.toString(buffer.array()));
		System.out.println("Buffer contents: " + new String(buffer.array()));
		System.out.println();
	}

	public static void main2(String[] args) {
		// // byte buffer if 4KB
		// ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

		// // create socket connection
		// SocketAddress address = new InetSocketAddress(SERVER_HOST, SERVER_PORT);
		// SocketChannel socketChannel = SocketChannel.open(address);
		// socketChannel.configureBlocking(true);
		
		// // print socket address
		// SocketAddress addr = socketChannel.getLocalAddress();
		// System.out.println(addr.toString());
		
		// // initialize
		// Scanner scanner = new Scanner(System.in);
		// String reply = "";
		// String line = "";
		
		// do {
		// 	// up this point, limit == capacity

		// 	// guarantees buffer will start on position 0
		// 	buffer.rewind();

		// 	// get input from console
		// 	System.out.print("Enter your input: ");
		// 	line = scanner.nextLine();

		// 	// write contents of that input into buffer
		// 	// Note: position will be updated
		// 	buffer.put(line.getBytes());

		// 	// we're going to work with buffer data on next operation
		// 	// use flip() so that we don't have to iterate on all bytes inside buffer
		// 	buffer.flip();

		// 	// sends bytes to the server
		// 	// will send bytes up to the specified limit (set by the flip())
		// 	socketChannel.write(buffer);

		// 	// use clear instead of flip() because amount of data that may arrive over the limit
		// 	// by using clear() position is set back to 0 and limit is set back to the capacity
		// 	buffer.clear();

		// 	// fills in the buffer
		// 	// most likely socket won't able to fill all buffer space
		// 	socketChannel.read(buffer);
			
		// 	// we'll work on data on the next operation
		// 	buffer.flip();

		// 	// get all bytes received by buffer when we did socketChannel.read() 
		// 	// using buffer.get() will change buffer's position
		// 	byte[] bytes = new byte[buffer.limit()];
		// 	buffer.get(bytes);

		// 	// at this point we will not work directly with the buffer
		// 	// so no need for flip() or clear()

		// 	// convert byte[] to String
		// 	reply = new String(bytes);
		// 	System.out.println("REPLY: " + reply);

		// 	// recycle the buffer without destroying the data inside
		// 	buffer.clear();

		// } while (reply.equals("x") == false);
		
		
		// socketChannel.close();
		// scanner.close();
	}
	

}
