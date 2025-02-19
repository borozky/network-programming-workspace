package threadcooperation;

import java.util.Scanner;

public class Playground {
	
	public static String line = "";
	static final Object LOCK = new Object();

	public static void main(String[] args) {
		
		new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + " started.");
			
			// don't create synchronized block here
			
			do {
				try {
					
					// let's wait for the object called 'LOCK' to 
					// release monitor lock
					synchronized(Playground.LOCK) {
						Playground.LOCK.wait();
					}
					
					// Task2.line will be modified by thread 'INPUT'
					// after INPUT thread calls 'notifyAll()', this line will execute
					System.out.println("[PRINTER] " + Playground.line);
					
				} 
				
				// This is just in case another thread interrupts this thread
				// This will just print Task2.line's current value
				catch (InterruptedException e) {
					System.err.println("[PRINTER] " + Playground.line);
				}
				
			} 
			// Exit when line is 'x'
			while (Playground.line.equals("x") == false);
			
			System.out.println(Thread.currentThread().getName() + " closed.");
			return;
			
		}, "PRINTER THREAD").start();
		
		new Thread(() -> {
			System.out.println(Thread.currentThread().getName() + " started.");
			
			Scanner scanner = new Scanner(System.in);
			
			// never use synchronized block here
			// else the 'PRINTER' thread will never run
			
			do {
				
				// wait until after input is received from console
				// by then, this thread notifies other thread waiting for Task2.LOCK
				synchronized (LOCK) {
					System.out.print("[INPUT] Enter your input: ");
					Playground.line = scanner.nextLine();
					Playground.LOCK.notifyAll();
				}
				
				// checks if line is not "x" or line is empty (line with spaces only is NOT empty)
				if (Playground.line.equals("x") == false || Playground.line.isEmpty()) {
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
			System.out.println(Thread.currentThread().getName() + " closed.");
			return;
			
		}, "INPUT THREAD").start();
	}

}
