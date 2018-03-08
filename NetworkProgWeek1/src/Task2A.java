import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class Task2A {

	public static void main(String[] args) {
		
		File outputFile = new File("output.txt");
		File checksumFile = new File("checksum.txt");
		
		System.out.println("TASK 2.1: Enter your input. Press 'x' then 'Enter' to exit.");
		
		try (
			InputStream input = new BufferedInputStream(System.in);
			
			// These ByteArrayOutputStream is there to make extracting
		    // string from stream of bytes much easier
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(); // for checking line by line. Will be reset() after each line. 
			ByteArrayOutputStream byteOutput2 = new ByteArrayOutputStream(); // All of the inputs except 'x\n' are stored here
			
			OutputStream fileOutput = new BufferedOutputStream(new FileOutputStream(outputFile));
			CheckedOutputStream checkedOutputStream = new CheckedOutputStream(byteOutput2, new CRC32());
			OutputStream checksumFileOutput = new BufferedOutputStream(new FileOutputStream(checksumFile));
		) {
			
			// check line by line for 'x\n'
			do {
				int data = input.read();
				while (data != -1) {
					if ((char) data == '\n') {
						break;
					}
					byteOutput.write(data);
					data = input.read();
				}
				
				// check for 'x'
				String line = byteOutput.toString(); 
				if (line.trim().toLowerCase().equals("x")) {
					break;
				}
				
				// append current line
				checkedOutputStream.write(line.getBytes());
				
				// Important. Without this, byteOutput.toString() will return multiple lines
				// and the if-statement above will never break
				byteOutput.reset(); 
				
			} while (true);
			
			
			// Write to checksum.txt and output.txt here
			fileOutput.write(byteOutput2.toString().getBytes());
			checksumFileOutput.write(Long.toString(checkedOutputStream.getChecksum().getValue()).getBytes());
			
			System.out.println("LINE: \n" + byteOutput2.toString());
			System.out.println("Checksum: " + checkedOutputStream.getChecksum().getValue());
			
		
			
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
