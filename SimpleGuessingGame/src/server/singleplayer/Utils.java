package server.singleplayer;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Utils {

	public static int getRandomNum(int min, int max) {
		return min + (int)(Math.random() * (max - min + 1));
	}
	
	public static String generate(int numDigits) {
		StringBuilder secretCode = new StringBuilder();
		while (secretCode.length() < numDigits) {
			int digit = getRandomNum(0, 9);
			String digitString = Integer.toString(digit);
			
			int index = secretCode.indexOf(digitString);
			if (index < 0) {
				secretCode.append(digitString);
			}
		}
		
		return secretCode.toString();
	}
	
	public static <T> Logger createLogger(String fileName, Class<T> className) {
		Logger logger = Logger.getLogger(Utils.class.getName());
		logger.setUseParentHandlers(false);
		
		
		FileHandler fileHandler = null;
		try {
			// handle logging through files. 'true' means append to existing log file
			fileHandler = new FileHandler(fileName, true);
			fileHandler.setLevel(Level.INFO);
			
			// use SimpleFormatter instead of default XMLFormatter
			fileHandler.setFormatter(new SimpleFormatter()); 
			
			logger.addHandler(fileHandler);
			return logger;
		} 
		catch (SecurityException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
		catch (IOException e) {
			logger.log(Level.SEVERE, "Logger is not working. ", e.getMessage());
		}
		
		return logger;
	}
}
