package core;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	List<GameRound> rounds = new ArrayList<>();
	private GameRound currentRound;
	private GameCallback cb;
	
	public Game(GameCallback callback) {
		setCallback(callback);
		cb.onStart(this);
	}
	
	public void setCallback(GameCallback callback) {
		cb = callback;
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
	
	
	// end the current round and start new one
	public GameRound startNextRound(int numDigits) {
		if (currentRound != null) {
			currentRound.end();
		}
		
		// create secret code
		String secretCode = createSecretCode(numDigits);
		cb.onSecretCodeCreated(this, secretCode);
		
		
		// start new round
		currentRound = new GameRound(secretCode);
		currentRound.setCallback(cb);
		rounds.add(currentRound);
		cb.onRoundStarted(this, currentRound);
		
		
		return getCurrentRound();
	}
	
	
	// sign up player in the current round
	public Player signUpPlayer(String playerName) throws Exception {
		Player player = new Player(playerName);
		
		if (currentRound == null) {
			throw new Exception("Current round is empty");
		}
		
		if (currentRound.getPlayers().size() == 6) {
			throw new Exception("Cannot add more than 6 players");
		}
		
		// Signing up player is adding player to current round
		currentRound.addPlayer(player);
		cb.onPlayerSignedUp(this, currentRound, player);
		
		return player;
	}
	
	
	// get players in current round
	public List<Player> getPlayers() {
		if (currentRound != null) {
			return currentRound.getPlayers();
		}
		return null;
	}
	
}









