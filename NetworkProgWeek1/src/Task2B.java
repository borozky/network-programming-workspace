import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

public class Task2B {

	public static void main(String[] args) {
		
		System.out.println("TASK 2.2");
		
		File sourceFile = new File("output.txt");
		
		try (
			InputStream fileInput = new BufferedInputStream(new FileInputStream(sourceFile));
			OutputStream output = new BufferedOutputStream(System.out);
			CheckedOutputStream checkedOutputStream = new CheckedOutputStream(output, new CRC32());
		) {
			
			int data = fileInput.read();
			while (data != -1) {
				checkedOutputStream.write(data);
				data = fileInput.read();
			}
			
			Long checksum = checkedOutputStream.getChecksum().getValue();
			System.out.println("Checksum: " + Long.toString(checksum));
			
			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
