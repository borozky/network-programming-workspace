
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
		
		System.out.print("Enter your input: ");
		
		try (
			InputStream input = new BufferedInputStream(System.in);
			OutputStream output = new BufferedOutputStream(System.out);
		) {
			
			int data = input.read();
			while(data != -1) {
				char character = (char) data;
				if (character == ' ') {
					character = '_';
				}
				
				// output
				output.write(character);
				output.flush(); // Important! Without this, the text won't be printed on the screen
				
				// read next character
				data = input.read();
			}
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Input lines of characters then writes them to a file. <br/>
	 * If line contains a single 'x', stop accepting input.<br/>
	 * Also the checksum of the input should be calculated and be written into another file<br/>
	 * Hint: Use <code>CheckedOutputStream</code> and <code>new Adler32() / new CRC32()</code>
	 */
	public static void task2A() {
		
		File outputFile = new File("output.txt");
		File checksumFile = new File("checksum.txt");
		
		System.out.print("Enter your input: ");
		
		try (
			InputStream input = new BufferedInputStream(System.in);
			OutputStream output = new BufferedOutputStream(new FileOutputStream(outputFile));
			OutputStream checksumOutput = new BufferedOutputStream(new FileOutputStream(checksumFile));
			CheckedOutputStream checkedOutput = new CheckedOutputStream(output, new CRC32());
		) {
			
			int data = input.read();
			while(data != -1) {
				if ((char) data == 'x') {
					break;
				}
				checkedOutput.write(data);
				checkedOutput.flush();
				
				if (input.available() == 0) {
					String checksumString = Long.toString(checkedOutput.getChecksum().getValue());
					checksumOutput.write(checksumString.getBytes());
					checksumOutput.flush();
					
					System.out.printf("Output sent to %s. Checksum version sent to %s.", outputFile.getName(), checksumFile.getName());
				}
				
				data = input.read();
			}
			
			System.out.println("Checksum: " + checkedOutput.getChecksum().getValue());
			
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void task2B () {
		
	}

}
