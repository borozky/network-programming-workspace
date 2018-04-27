package urlconnection;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;

/**
 * Program that prints the type, length and contents of a URL resource. 
 * <p>Note: In order to access the URL resource, you should run 
 * this program inside the moonshot server. 
 */
public class Program {
	
	public static final String URL_ADDRESS = "http://m1-c45n1.csit.rmit.edu.au/~Course/index.php";

	public static void main(String[] args) {
		
		URL url = null;
		URLConnection urlConnection = null;
		String host = null;
		InetAddress inetAddress = null;
		StringBuilder stringBuilder = null;
		InputStream inputStream = null;
		
		try {
			stringBuilder = new StringBuilder();
			url = new URL(URL_ADDRESS);
			
			// get IP address
			host = url.getHost();
			inetAddress = InetAddress.getByName(host);
			System.out.printf("IP Address: %s\n", inetAddress.getHostAddress());
			
			
			// connect to server
			urlConnection = url.openConnection();
			
			
			// content length
			int contentLength = urlConnection.getContentLength();
			System.out.printf("CONTENT-LENGTH: %d\n", contentLength);
			
			
			// content type
			String contentType = urlConnection.getContentType();
			System.out.printf("CONTENT-TYPE: %s\n", contentType);
			
			
			inputStream = urlConnection.getInputStream();
			
			// get actual content by reading byte by byte
			if (contentLength > 0) {
				int data = inputStream.read();
				while (data != -1) {
					stringBuilder.append((char) data);
					data = inputStream.read();
				}
				System.out.println(stringBuilder.toString());
			}
			
			
		} 
		catch (UnknownHostException e) {
			System.err.println("URL cannot be found. " + e.getMessage());
		}
		catch (MalformedURLException e) {
			System.err.println("URL is invalid. " + e.getMessage());
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (inputStream != null) inputStream.close();
			} catch (IOException e) {
				System.err.println("Sorry, something went wrong while closing the connection." + e.getMessage());
			}
		}
		
		
	}

}
