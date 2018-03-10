import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Task1 {

	public static void main(String[] args) {
		
		System.out.println("TASK 1: Enter your input:");
		
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
				
				// write() only stores data in the buffer
				// flush() is the method that pushes the data from buffer to the console
				output.write(character); 
				output.flush();
				
				// read next character
				data = input.read();
			}
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
