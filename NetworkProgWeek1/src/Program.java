
import java.io.*;
import java.util.*;
import java.util.zip.*;

public class Program {

	public static void main(String[] args) {
		//task1();
		task2A();
		//task2B();
	}
	
	/**
	 * Outputs input characters to the screen using InputStream and OutputStream
	 * All whitespace characters are replaced with underscores
	 */
	public static void task1(){
		
		InputStream input = null;
		OutputStream output = null;
		
		try {
			input = new BufferedInputStream(System.in);
			output = new BufferedOutputStream(System.out);
			
			
			int data = input.read();
			while(data != -1) {
				char character = (char) data;
				if (Character.isWhitespace(character)) {
					character = '_';
				}
				
				// output
				output.write(character);
				output.flush(); // Important! Without this, the text won't be printed on the screen
				
				// read next character
				data = input.read();
			}
			
			
		} catch(IOException e) {
			e.printStackTrace();
		} 
		
		// close connection; check for nulls
		finally {
			try {
				if (input != null) input.close();
				if (output != null) output.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * Input lines of characters then writes them to a file. <br/>
	 * If line contains a single 'x', stop accepting input.<br/>
	 * Also the checksum of the input should be calculated and be written into another file<br/>
	 * Hint: Use <code>CheckedOutputStream</code> and <code>new Adler32() / new CRC32()</code>
	 */
	public static void task2A() {
		
		InputStream input = null;
		OutputStream output = null;
		CheckedOutputStream checkedOutput = null;
		
		try {
			input = new BufferedInputStream(System.in);
			output = new BufferedOutputStream(System.out);
			checkedOutput = new CheckedOutputStream(System.out, new CRC32());
			
			int data = input.read();
			int available = input.available();
			byte[] bytes = new byte[available];
			
			while(data != -1) {
				
			}
			
			
			
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (input != null) input.close();
				if (output != null) output.close();
				if (checkedOutput != null) checkedOutput.close();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
		
		
		
	}
	
	public static void task2B() {
		
	}

}
