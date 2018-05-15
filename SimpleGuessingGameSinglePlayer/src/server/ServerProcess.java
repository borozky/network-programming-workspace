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

public class ServerProcess {
	
	private Socket socket;
	private ServerCallback cb;
	private ObjectOutputStream stream;
	private BufferedReader reader;
	private Game game;
	private GameManager manager;
	
	public ServerProcess(Game game, Socket socket, ServerCallback callback, GameCallbackLoggerImpl gameLoggerCallback) throws IOException {
		this.game = game;
		this.socket = socket;
		this.cb = callback;
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.manager = new GameManager(game);
		
		
		GameCallback cb = new GameCallbackImpl(stream);
		manager.addCallback(cb);
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
	
	
	public void run() {
		
		try {
			
			if (manager.getCurrentPlayer() == null) {
				String name = readline("Enter your name: ", true);
				manager.setCurrentPlayerName(name);
			}
			
			if (manager.getNumDigits() < 3 || manager.getNumDigits() > 8) {
				int digits = getNumDigits();
				manager.setNumDigits(digits);
			}
			
			manager.startNextRound();
			
			
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
			
			manager.endCurrentRound();
					
			boolean shouldContinue = continueOrQuit("Press (p) to continue to play, or (q) to quit: ");
			
			if (!shouldContinue) {
				manager.quitPlayer();
			} else {
				run();
			}
			
		} catch (IOException e) {
			cb.onException(this, e);
		} catch (Exception e) {
			cb.onException(this, e);
		} finally {
			close();
		}
	}
	
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
	
	public int getNumDigits() throws IOException {
		int numDigits = 0;
		numDigits = readInt("Enter number of digits: ");
		if (numDigits < Game.MIN_DIGITS || numDigits > Game.MAX_DIGITS) {
			sendError(String.format("Number of digits must be %d - %d.", Game.MIN_DIGITS, Game.MAX_DIGITS));
			return getNumDigits();
		}
		
		return numDigits;
		
	}

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
	
	
	private void reply(String message) throws IOException {
		Response response = Response.message(message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
	
	private String readline(String message) throws IOException {
		return readline(message, false);
	}

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
	
	private boolean yesOrNo(String message) throws IOException  {
		String response = "";
		
		response = readline(message);
		response = response.toLowerCase();
		
		if (response.equals("y") || response.equals("yes")) {
			return true;
		}
		else if (response.equals("n") || response.equals("no")) {
			return false;
		}
		else {
			sendError(response + " is not a valid response.");
			return yesOrNo(message);
		}
	}
	
	private void sendError(String message) throws IOException {
		Response response = Response.message("ERROR: " + message);
		stream.writeObject(response);
		cb.onSendResponse(this, response);
	}
	
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
