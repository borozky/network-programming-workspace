package threadcooperation;

import java.util.Scanner;

/**
 * Input thread checks for console inputs.
 * If character is detected, this thread 
 * notifies the printer thread. If 'x' is 
 * detected this thread terminates.
 * 
 * @author user
 */
public class InputThread implements Runnable {
	
	public static final Object LOCK = new Object();
	static String line = "";
	
	@Override
	public void run() {
		
		String threadName = Thread.currentThread().getName();
		System.out.println(threadName + " started.");
		
		Scanner scanner = new Scanner(System.in);
		
		// never use synchronized block here
		// else the 'PRINTER THREAD' will be stuck waiting forever
		
		do {
			
			// wait until after input is received from console
			// by then, this thread notifies other thread waiting for Task2.LOCK
			synchronized (LOCK) {
				System.out.printf("[%s] Enter your input: ", threadName);
				line = scanner.nextLine();
				LOCK.notifyAll();
				
				
			} // monitor lock released here in the ending curly brace '}'
			
			// checks if line is not "x" or line is empty (line with spaces only is NOT empty)
			if (line.toLowerCase().equals("x") == false || line.isEmpty()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} 
		while (line.toLowerCase().equals("x") == false);
		
		// EXIT thread 'INPUT'
		scanner.close();
		System.out.println(threadName + " closed.");
		return;
	}
}
