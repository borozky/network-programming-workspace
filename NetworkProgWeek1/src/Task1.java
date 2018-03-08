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

}
