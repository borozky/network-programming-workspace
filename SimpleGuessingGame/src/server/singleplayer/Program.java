package server.singleplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Program {
	
	public static void main(String[] args) {
		
		ServerCallback serverCallback = new ServerCallbackImpl(Server.communicationsLogger);
		Game game = new Game();
		Server server = null;
		int port = getPortNumber(args);
		
		try {
			server = new Server(port, game, serverCallback);
			
			do {
				try {
					server.listen();
					server.startGame();
					
					do {
						GameRound round = server.newRound();
						server.startGameLoop(round);
					} 
					while (server.shouldContinue());
					
					
				} catch (java.net.SocketException e) {
					serverCallback.onException(server, e);
					continue;
				} catch (Exception e) {
					serverCallback.onException(server, e);
					continue;
				}
				
			} while(true);
			
		}
		catch (IOException e) {
			serverCallback.onException(server, e);
		} finally {
			try {
				if (server != null) server.close();
			} catch (IOException e) {
				serverCallback.onException(server, e);
			}
		}
		
	}

	
	
	/**
	 * Get the first port number provided by command line arguments 
	 * (main(String[] args))
	 * <p>
	 * The first port number will be used to receive client messages
	 * 
	 * @param args If there are no args provided (ie. empty array), 
	 * 			   it will use the default port 15376
	 * @return int
	 */
	public static int getPortNumber(String[] args) {
		int portNumber = Server.DEFAULT_PORT_NUMBER;
		
		if (args.length < 1) {
			return portNumber;
		}
		
		try {
			portNumber = Integer.parseInt(args[0]);
		}
		catch (NumberFormatException numFormatEx) {
			System.err.printf("ERROR: %s is not a valid port number\n", args[0]);
		}
		
		return portNumber;
	}

}
