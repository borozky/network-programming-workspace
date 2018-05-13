package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import core.GameCallbackImpl;
import core.GameCallbackLoggerImpl;

public class ServerProcess implements Runnable {
	
	public static final Object LOCK = new Object();
	
	private Socket socket;
	private GameManager manager;
	private ServerCallback cb;
	private ObjectOutputStream stream;
	private BufferedReader reader;
	
	public ServerProcess(GameManager manager, Socket socket, ServerCallback callback) throws IOException {
		this.manager = manager;
		this.socket = socket;
		this.cb = callback;
		this.stream = new ObjectOutputStream(socket.getOutputStream());
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	
		manager.addCallback(new GameCallbackImpl(stream));
		manager.addCallback(new GameCallbackLoggerImpl());
	}
	
	
	@Override
	public void run() {
		
		try {
			
			String name = readline("Enter your name: ", true);
			manager.setPlayerName(name);
			
			
			if (manager.isCurrentPlayerFirst() == false) {
				if (manager.getNumDigits() < 3 || manager.getNumDigits() > 8) {
					reply("Waiting for the first player to setup num of digits");
				}
			}
			
			
			synchronized (LOCK) {
				if (manager.getNumDigits() < 3 || manager.getNumDigits() > 8) {
					int digits = getNumDigits();
					manager.setNumDigits(digits);
				}
			}
			
			
			synchronized (LOCK) {
				if (manager.getNumPlayers() < 3) {
					reply("Waiting for other players...");
					LOCK.wait();
				}
				
				if (manager.getNumPlayers() >= 3) {
					LOCK.notifyAll();
				}
			}
			
			
			synchronized (LOCK) {
				manager.startNextRound();
			}
			
			
			do {
				String guess = readline("Enter your guess: ");
				manager.addGuess(guess);
				
				if (manager.playerWins() || manager.playerLoses()) {
					break;
				}
				
				
				// TODO: Continue or quit
				
			} while (true);
			
			
			
			
			
			/*
			// ask for player name
			String playerName = readline("Enter your name: ", true);
			reply("Your player name is " + playerName);
			player = game.signUpPlayer(playerName);
			
			// PLAYER SIGNED UP
			gameCb.onPlayerSignedUp(game, null, player);
			
			GameRound round = null;
			
			
			if (game.getPlayers().get(0) != player ) {
				reply("Waiting for the first player to setup num of digits");
			}
			
			synchronized (LOCK) {
				if (game.getNumDigits() < 3 || game.getNumDigits() > 8) {
					int numDigits = getNumDigits();
					game.setNumDigits(numDigits);
				}
			}
			
			synchronized (LOCK) {
				if (game.getPlayers().size() < 3) {
					reply("Waiting for other players...");
					LOCK.wait();
				} 
				
				if (game.getPlayers().size() >= 3) {
					LOCK.notifyAll();
				}
			}
			
			synchronized (LOCK) {
				if (game.getCurrentRound() == null) {
					round = game.startNextRound();
					gameCb.onSecretCodeCreated(game, round.getSecretCode());
				}
			}
			
			// ROUND STARTED
			gameCb.onRoundStarted(game, round);
			
			do {
				
				String guess = readline("Enter your guess: ");
				round.addGuess(player, guess);
				gameCb.onGuessAdded(round, player, guess);
				
				if (player.hasWon(round)) {
					gameCb.onPlayerWon(round, player, player.getNumGuesses());
					break;
				} else if (player.hasLost(round)) {
					gameCb.onPlayerLost(round, player, round.getSecretCode());
					break;
				}
				
			} while (true);
			*/
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			manager.removeCurrentPlayer();
			
			close();
		}
	}
	
	public int getNumDigits() throws IOException {
		int numDigits = 0;
		numDigits = readInt("Enter number of digits: ");
		if (numDigits < 3 || numDigits > 8) {
			sendError("Number of digits must be 3 - 8.");
			return getNumDigits();
		}
		
		return numDigits;
		
	}

	public void close() {
		try {
			if (stream != null) stream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
		} catch (IOException e) {
			cb.onException(this, e);
		}
	}
	
	
	private void reply(String message) throws IOException {
		stream.writeObject(new Response(message));
	}
	
	private String readline(String message) throws IOException {
		return readline(message, false);
	}

	private String readline(String message, boolean isRequired) throws IOException {
		stream.writeObject(new Response(message, Response.READLINE));
		
		String line = reader.readLine();
		if (isRequired) {
			while (line.trim().isEmpty()) {
				sendError("Please enter non empty input.");
				stream.writeObject(new Response(message, Response.READLINE));
				line = reader.readLine();
			}
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
		stream.writeObject(new Response("ERROR: " + message));
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
