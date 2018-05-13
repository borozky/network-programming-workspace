package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import core.Game;
import core.GameCallback;
import core.GameCallbackImpl;
import core.GameRound;
import core.Player;

public class ServerProcess implements Runnable {
	
	public static final Object LOCK = new Object();
	
	// required fields
	private Game game;
	private Socket socket;
	private ServerCallback cb;
	
	// derived fields
	private ObjectOutputStream objectOutputStream;
	private BufferedReader reader;
	private Player player;
	
	public ServerProcess(Game game, Socket socket, ServerCallback callback) throws IOException {
		this.game = game;
		this.socket = socket;
		cb = callback;
		
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		GameCallback cb = new GameCallbackImpl(this);
		game.addCallback(cb);	
	}
	
	public ObjectOutputStream getObjectOutputStream() {
		return objectOutputStream;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	
	@Override
	public void run() {
		
		try {
			
			// ask for player name
			String playerName = readline("Enter your name: ", true);
			reply("Your player name is " + playerName);
			player = game.signUpPlayer(playerName);
			
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
					game.startNextRound();
				}
			}
			
			round = game.getCurrentRound();
			
			do {
				String guess = readline("Enter your guess: ");
				round.addGuess(player, guess);
				
				if (player.hasWon(round) || player.hasLost(round)) {
					break;
				}
			} 
			while (true);
			
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
			if (objectOutputStream != null) objectOutputStream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
		} catch (IOException e) {
			cb.onException(this, e);
		}
	}
	
	
	public void reply(String message) throws IOException {
		objectOutputStream.writeObject(new Response(message));
	}
	
	public String readline(String message) throws IOException {
		return readline(message, false);
	}

	public String readline(String message, boolean isRequired) throws IOException {
		objectOutputStream.writeObject(new Response(message, Response.READLINE));
		
		String line = reader.readLine();
		if (isRequired) {
			while (line.trim().isEmpty()) {
				sendError("Please enter non empty input.");
				objectOutputStream.writeObject(new Response(message, Response.READLINE));
				line = reader.readLine();
			}
		}
		
		return line.trim();
	}
	
	public boolean yesOrNo(String message) throws IOException  {
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
	
	public void sendError(String message) throws IOException {
		objectOutputStream.writeObject(new Response("ERROR: " + message));
	}
	
	public int readInt(String message) throws IOException {
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
