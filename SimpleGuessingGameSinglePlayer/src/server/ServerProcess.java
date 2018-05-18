package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Logger;

import core.Game;
import core.GameCallback;
import core.GameCallbackImpl;
import core.GameCallbackLoggerImpl;
import core.GameManager;

/**
 * Class that decided how the game play will run. 
 * You can treat this as the main controller of the game project.
 * The game will use serialization and ObjectOutputStream. This allows 
 * for multiline printing in the client side and it makes sending commands to 
 * the client possible.
 * 
 * @author user
 *
 */
public class ServerProcess {
	
	// Field that are required for the game to run
	private Game game;
	private Socket socket;
	private ServerCallback cb;
	
	// derived fields
	private ObjectOutputStream stream;
	private BufferedReader reader;
	private GameManager manager;
	
	public ServerProcess(Game game, Socket socket, ServerCallback callback, GameCallbackLoggerImpl gameLoggerCallback) throws IOException {
		this.game = game;
		this.socket = socket;
		this.cb = callback;
		
		// we will serialize our response, so use ObjectOutputStream
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.manager = new GameManager(game);
		
		
		// send server responses through GameCallbackImpl callback class
		GameCallback cb = new GameCallbackImpl(stream);
		manager.addCallback(cb);
		
		// add callback that logs game events to the log file
		manager.addCallback(gameLoggerCallback);
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public Game getGame() {
		return game;
	}
	
	public GameManager getGameManager() {
		return manager;
	}
	
	
	/**
	 * Server process main loop. This method will do these steps.<ol>
	 * <li>Ask for player's name. This will happen only on the first time.</li>
	 * <li>Ask for number of digits for the secret code if number. This will only happen once.</li>
	 * <li>Starts the round</li>
	 * <li>Ask the user to enter the guess. This part loops until player forfeits, wins or loses</li>
	 * <li>Ends the round</li>
	 * <li>Ask the player is they want to continue or quit. 
	 * If the player decides to continue, the process repeats again (#3) or this process closes and return</li>
	 * <ol>
	 */
	public void begin() {
		
		try {
			
			// ask for player name
			if (manager.getCurrentPlayer() == null) {
				String name = readline("Enter your name: ", true);
				manager.setCurrentPlayerName(name);
			}
			
			// ask for number of digits the secret code will have
			if (manager.getNumDigits() < Game.MIN_DIGITS || manager.getNumDigits() > Game.MAX_DIGITS) {
				int digits = getNumDigits();
				manager.setNumDigits(digits);
			}
			
			// begin round. At this point the secret code is generated
			manager.startNextRound();
			
			
			// ask for player's guesses
			do {
				String guess = readline("Enter your guess: ");
				manager.addGuess(guess);
				
				if (manager.isCurrentPlayerForfeited()) {
					break;
				}
				
				if (manager.playerWins() || manager.playerLoses()) {
					break;
				}
				
			} while (true);
			
			// end the round. If round has not ended, it will be ended automatically
			manager.endCurrentRound();
			
			// ask player if they want to play again or quit
			boolean shouldContinue = continueOrQuit("Press (p) to continue to play, or (q) to quit: ");
			
			if (!shouldContinue) {
				manager.quitPlayer();
			} else {
				
				// run this method again recursively
				begin();
			}
			
		} catch (IOException e) {
			cb.onException(this, e);
		} catch (Exception e) {
			cb.onException(this, e);
		} finally {
			close();
		}
	}
	
	/**
	 * Ask client if they want to continue or not. 
	 * Will ask again until 'p' or 'q' is entered
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	public boolean continueOrQuit(String message) throws IOException {
		String reply = readline(message, true);
		reply = reply.trim().toLowerCase();
		if (reply.equals("p")) {
			return true;
		}
		else if (reply.equals("q")) {
			return false;
		}
		else {
			sendError("Please enter 'p' or 'q'.");
			return continueOrQuit(message);
		}
	}
	
	/**
	 * Ask user for number of digits the secret code will have
	 * 
	 * @return
	 * @throws IOException
	 */
	public int getNumDigits() throws IOException {
		int numDigits = 0;
		numDigits = readInt("Enter number of digits: ");
		if (numDigits < Game.MIN_DIGITS || numDigits > Game.MAX_DIGITS) {
			sendError(String.format("Number of digits must be %d - %d.", Game.MIN_DIGITS, Game.MAX_DIGITS));
			return getNumDigits();
		}
		
		return numDigits;
		
	}

	/**
	 * Close connection
	 */
	public void close() {
		manager.removeCurrentPlayer();
		
		try {
			if (stream != null) stream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
			
			cb.onClientDisconnected(null, socket, this);
			
		} catch (IOException e) {
			cb.onException(this, e);
		}
	}
	
	
	/**
	 * Helper method to reply messages to the client. 
	 * Uses serialization to send objects to the client. 
	 * The client then unpacks the contents of the serialized object
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void reply(String message) throws IOException {
		Response response = Response.message(message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
	
	/**
	 * Overload of readline(String, boolean) method
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	private String readline(String message) throws IOException {
		return readline(message, false);
	}

	
	/**
	 * Helper methods that asks users for input. 
	 * You can pass a message as first argument, and boolean value 
	 * for the second argument to check if input is required. 
	 * If the client sends an empty string, the method will recursively 
	 * call itself again until input sent is not an empty string 
	 * 
	 * @param message
	 * @param isRequired
	 * @return
	 * @throws IOException
	 */
	private String readline(String message, boolean isRequired) throws IOException {
		Response response = Response.readLine(message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
		
		String line = reader.readLine();
		cb.onClientReply(this, line);
		
		if (isRequired && line.trim().isEmpty()) {
			sendError("Please enter non empty input.");
			return readline(message, isRequired);
		}
		
		return line.trim();
	}

	
	/**
	 * Send a message to the client. Message is prepended with "ERROR: "
	 * 
	 * @param message
	 * @throws IOException
	 */
	private void sendError(String message) throws IOException {
		Response response = Response.message("ERROR: " + message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
	
	/**
	 * Read numbers from client. If received input is not a number, 
	 * it will ask again
	 * 
	 * @param message
	 * @return
	 * @throws IOException
	 */
	private int readInt(String message) throws IOException {
		String reply = readline(message, true);
		int num = 0;
		
		try {
			num = Integer.parseInt(reply.trim());
			return num;
		} catch (NumberFormatException e) {
			sendError(reply + " is not a valid number");
			return readInt(message);
		}
	}
}
