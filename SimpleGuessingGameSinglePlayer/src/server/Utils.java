package server;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Utils {

	public static <T> Logger createLogger(String fileName, Class<T> className) {
		Logger logger = Logger.getLogger(className.getName());
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
