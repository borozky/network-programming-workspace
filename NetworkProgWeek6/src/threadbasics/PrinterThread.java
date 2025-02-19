package threadbasics;

import java.util.Scanner;

/**
 * Printer thread prints the volatile 'message'
 * variable from the console, then exits immediately.
 * 
 * @author user
 */
public class PrinterThread implements Runnable {
	
	// message will be changed by main thread, so use volatile
	private volatile String message;

	
	// exits when message is supplied
	@Override
	public void run() {
		while (message == null) {
			continue;
		}
		System.out.println(message);
	}
	
	// setter method to pass message from console
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Main thread scans inputs and modifies 
	 * 'message' field from the PrinterThread 
	 * object. After that, the main thread 
	 * exits immediately.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// run the thread
		PrinterThread printer = new PrinterThread();
		Thread printerThread = new Thread(printer);
		printerThread.start();
		
		// get input from console
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter your input: ");
		String message = scanner.nextLine();
		
		// when message is supplied, the run() method in PrinterThread returns
		printer.setMessage(message);
		scanner.close();
	}

}
