package task2;

import java.util.Scanner;

class PrinterThread implements Runnable {
	
	@Override
	public void run() {
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " started.");
		
		// don't create synchronized block here
		
		do {
			try {
				
				// let's wait for the object called 'LOCK' to 
				// release monitor lock
				synchronized(InputThread.LOCK) {
					InputThread.LOCK.wait();
				}
				
				// Task2.line will be modified by thread 'INPUT'
				// after INPUT thread calls 'notifyAll()', this line will execute
				System.out.printf("[%s] %s\n", threadName, InputThread.line);
				
			} 
			
			// This is just in case another thread interrupts this thread
			// This will just print Task2.line's current value
			catch (InterruptedException e) {
				System.err.printf("[%s] %s\n", threadName, InputThread.line);
			}
			
		} 
		// Exit when line is 'x'
		while (InputThread.line.equals("x") == false);
		
		System.out.println(threadName + " closed.");
		return;
	}
}


class InputThread implements Runnable {
	
	public static final Object LOCK = new Object();
	static String line = "";
	
	@Override
	public void run() {
		
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " started.");
		
		Scanner scanner = new Scanner(System.in);
		
		// never use synchronized block here
		// else the 'PRINTER' thread will stuck waiting forever
		
		do {
			
			// wait until after input is received from console
			// by then, this thread notifies other thread waiting for Task2.LOCK
			synchronized (LOCK) {
				System.out.printf("[%s] Enter your input: ", threadName);
				line = scanner.nextLine();
				LOCK.notifyAll();
				
				
			} // monitor lock released here in the ending curly brace '}'
			
			// checks if line is not "x" or line is empty (line with spaces only is NOT empty)
			if (line.equals("x") == false || line.isEmpty()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} 
		while (line.equals("x") == false);
		
		// EXIT thread 'INPUT'
		scanner.close();
		System.out.println(threadName + " closed.");
		return;
	}
}

public class Program {
	
	static Thread printerThread, inputThread;
	static PrinterThread printer;
	static InputThread input;

	public static void main(String[] args) {
		printer = new PrinterThread();
		input = new InputThread();
		
		printerThread = new Thread(printer, "PRINTER THREAD");
		inputThread = new Thread(input, "INPUT THREAD");
		
		inputThread.start();
		printerThread.start();
	}

}
