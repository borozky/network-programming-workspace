package threadcooperation;

/**
 * Invokes 2 threads<br/>
 * - Input thread which accepts inputs from console.<br/>
 * - Printer thread which prints inputs coming from the input thread
 * 
 * @author Joshua Orozco
 */
public class Program {

	public static void main(String[] args) {
		Runnable printer = new PrinterThread();
		Runnable input = new InputThread();
		
		Thread printerThread = new Thread(printer, "PRINTER THREAD");
		Thread inputThread = new Thread(input, "INPUT THREAD");
		
		inputThread.start();
		printerThread.start();
	}

}
