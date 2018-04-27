package jarurlconnection;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Program {
	
	public static final String JAR_URL_ADDRESS = "jar:http://m1-c45n1.csit.rmit.edu.au//~Course/HelloWorld.jar!/";

	public static void main(String[] args) {
		
		URL jarUrl = null;
		JarURLConnection jarURLConnection = null;
		JarFile jarFile = null;
		
		try {
			jarUrl = new URL(JAR_URL_ADDRESS);
			jarURLConnection = (JarURLConnection) jarUrl.openConnection();
			
			// content type
			String contentType = jarURLConnection.getContentType();
			System.out.printf("CONTENT-TYPE: %s\n", contentType);
			
			// content length
			int contentLength = jarURLConnection.getContentLength();
			System.out.printf("CONTENT-LENGTH: %d\n", contentLength);
			
			// get entries
			jarFile = jarURLConnection.getJarFile();
			Enumeration<JarEntry> jarEntries = jarFile.entries();
			
			// iterate each entry
			while (jarEntries.hasMoreElements()) {
				JarEntry entry = jarEntries.nextElement();
				String entryName = entry.getName();
				long entrySize = entry.getSize();
				System.out.printf("%s (%d bytes)\n", entryName, entrySize);
			}
		}
		catch (UnknownHostException e) {
			System.err.println("JAR cannot be found. " + e.getMessage());
		}
		catch (MalformedURLException e) {
			System.err.println("Invalid url. " + e.getMessage());
		}
		catch (IOException e) {
			System.err.println(e.getMessage());
		}
		finally {
			
			// nothing to close here
			
		}
		

	}

}
