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
		
		System.out.println(
			"TASK 2.1: Enter your input. Press 'x' then 'Enter' to exit."
		);
		
		try (
			InputStream input = new BufferedInputStream(System.in);
			
			// The ByteArrayOutputStream are convenient objects to 
			//    extract strings from the input.
			// The first BAOS is just for checking line-by-line. Each 
			//    line, the first BAOS will be reset
			// The second one is to store the whole input
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			ByteArrayOutputStream byteOutput2 = new ByteArrayOutputStream();

			// Use this to write to 2nd BAOS and to calculate checksum automatically
			CheckedOutputStream checkedOutputStream = new CheckedOutputStream(
				byteOutput2, new CRC32()
			);
			
			// Write to files 'output.txt' and 'checksum.txt'
			OutputStream fileOutput = new BufferedOutputStream(
				new FileOutputStream(outputFile)
			);
			OutputStream checksumFileOutput = new BufferedOutputStream(
				new FileOutputStream(checksumFile)
			);				
		) {
			
			// continue until a line with 'x\n' is found
			do {
				
				// check line by line
				int data = input.read();
				while (data != -1) {
					if ((char) data == '\n') {
						break;
					}
					byteOutput.write(data);
					data = input.read();
				}
				
				// check for 'x' or 'X'
				String line = byteOutput.toString(); 
				if (line.trim().toLowerCase().equals("x")) {
					break;
				}
				
				// append current line
				checkedOutputStream.write(line.getBytes());
				
				// Important. Without this, byteOutput.toString() will return 
				// multiple lines and the if-statement above will never break
				byteOutput.reset(); 
				
			} while (true);
			
			
			// Write to checksum.txt and output.txt here
			fileOutput.write(byteOutput2.toString().getBytes());
			
			// retrieve the checksum, then write to checksum.txt
			long checksum = checkedOutputStream.getChecksum().getValue();
			String checksumStr = Long.toString(checksum);
			checksumFileOutput.write(checksumStr.getBytes());
			
			// For debugging
			System.out.println("LINE: \n" + byteOutput2.toString());
			System.out.println("Checksum: " + checkedOutputStream.getChecksum().getValue());
		} 
		catch (IOException e) {
			e.printStackTrace();
		}

	}

}
