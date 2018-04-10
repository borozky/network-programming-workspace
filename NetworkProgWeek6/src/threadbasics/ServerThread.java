package threadbasics;

import java.util.Scanner;

public class ServerThread implements Runnable {

	@Override
	public void run() {
		Scanner scanner = new Scanner(System.in);
		do {
			
			// A dirty hack to prevent messages from displaying out of order on the console
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// Read line from console
			System.out.print("[SERVER THREAD] Enter your input: ");
			String line = scanner.nextLine();
			
			// Modify the message global variable
			message = line;
			
			// Exit and kills the thread
			if (line.equals("x")) break;
			
		} while(true);
		
		scanner.close();
		System.out.println("[SERVER THREAD EXITED]");
	}
	
	// Use volatile so that all threads will have same state of the message variable
	// prevents thread caching this variable
	static volatile String message;
	
	public static void main(String[] args) {
		
		// Start the server.
		// Server will accept inputs from console
		(new Thread(new ServerThread())).start();
		
		// Main thread 
		// This checks continuously if message is modified
		while (true) {
			
			// skip if message is not modified
			if (message == null) continue;
			
			// kill the main thread
			if (message.equals("x")) break;
			
			// print to console if message has been changed
			System.out.println("[MAIN] " + message);
			
			// prevents console from print too many times
			message = null;
		}
		
		System.out.println("[MAIN EXITED]");
	}

}
