package core;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	public static final int NOT_STARTED = 0;
	public static final int STARTED = 1;
	
	List<GameRound> rounds = new ArrayList<>();
	private GameRound currentRound;
	
	private List<GameCallback> callbacks = new ArrayList<>();
	
	
	private List<Player> players = new ArrayList<>();
	private int numDigits = 0;
	
	public Game() {
		
	}
	
	public void start() {
		//callbacks.forEach(c -> c.onStart(this));
	}
	
	public void addCallback(GameCallback callback) {
		//callbacks.add(callback);
	}
	
	public synchronized int getNumDigits() {
		return numDigits;
	}
	
	public synchronized void setNumDigits(int numDigits) {
		this.numDigits = numDigits;
	}
	
	// generate secret code
	public String createSecretCode(int numDigits) {
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
	
	
	public int getRandomNum(int min, int max) {
		return min + (int)(Math.random() * (max - min + 1));
	}
	
	public GameRound getCurrentRound() {
		return currentRound;
	}
	
	public void removePlayer(Player player) {
		players.remove(player);
	}
	
	
	// end the current round and start new one
	public GameRound startNextRound() throws Exception {
		if (currentRound != null && !currentRound.hasEnded() ) {
			throw new Exception("Current round has not yet ended");
		}
		
		if (players.size() == 0) {
			throw new Exception("There are currently no players available");
		}
		
		// create secret code
		String secretCode = createSecretCode(numDigits);
		//callbacks.forEach(c -> c.onSecretCodeCreated(this, secretCode));
		
		
		// start new round
		currentRound = new GameRound(secretCode);
		//callbacks.forEach(c -> currentRound.addCallback(c));
		
		// add players
		for (Player p : players) {
			currentRound.addPlayer(p);
		}
		
		rounds.add(currentRound);
		//callbacks.forEach(c -> c.onRoundStarted(this, currentRound));
		
		
		return getCurrentRound();
	}
	
	
	// sign up player in the current round
	public synchronized Player signUpPlayer(String playerName) throws Exception {
		if (getPlayers().size() == 6) {
			throw new Exception("Cannot add more than 6 players");
		}
		
		Player player = new Player(playerName);
		players.add(player);
		//callbacks.forEach(c -> c.onPlayerSignedUp(this, currentRound, player));
		
		return player;
	}
	
	
	// get players in current round
	public List<Player> getPlayers() {
		return players;
	}
	
}









