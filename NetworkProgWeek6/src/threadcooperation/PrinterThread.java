package threadcooperation;

/**
 * <p>Printer thread listens for any interrupts 
 * (or notification) from the input thread. 
 * Also, inputs received by Input thread from 
 * the console will be printed out.<br/>
 * <p>If the thread receives a single 'x' character,
 * this thread terminates.
 * 
 * @author Joshua Orozco
 */
public class PrinterThread implements Runnable {
	
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
				
				// InputThread.line will be modified by thread 'INPUT THREAD'
				// after INPUT THREAD calls 'notifyAll()', this line will execute
				System.out.printf("[%s] %s\n", threadName, InputThread.line);
				
			} 
			
			// This is just in case another thread interrupts this thread
			// This will just print InputThread.line's current value
			catch (InterruptedException e) {
				System.err.printf("[%s] %s\n", threadName, InputThread.line);
			}
			
		} 
		// Exit when line is 'x'
		while (InputThread.line.toLowerCase().equals("x") == false);
		
		System.out.println(threadName + " closed.");
		return;
	}
}
