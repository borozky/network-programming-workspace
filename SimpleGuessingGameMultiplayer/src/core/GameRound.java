package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import core.Player.PlayerStatus;

public class GameRound {
	
	public static final int MAX_ATTEMPTS = 10;
	
	private String secretCode;
	
	List<Player> players = new ArrayList<>();
	List<Player> winners = new ArrayList<>();
	List<Player> losers = new ArrayList<>();
	List<Player> forfeited = new ArrayList<>();
	
	List<String> guesses = new ArrayList<>();
	
	private boolean hasEnded = false;
	
	
	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	public void addPlayer(Player player) {
		player.clearAllGuesses();
		players.add(player);
	}
	
	public String getSecretCode() {
		return secretCode;
	}

	
	public List<Player> getPlayers() {
		return players;
	}

	
	public List<String> getGuesses() {
		return guesses;
	}
	
	
	public int getNumDigits() {
		return secretCode.trim().length();
	}
	
	
	public void addGuess(Player player, String guess) {
		// if player won, no need to add guess
		if (hasWinner(player) || hasLoser(player)) {
			return;
		}
		
		// add guess
		if (players.contains(player) && player.getNumGuesses() < MAX_ATTEMPTS) {
			player.addGuess(guess);
			guesses.add(guess);
			//callbacks.forEach(c -> c.onGuessAdded(this, player, guess));
		}
		
		// if guess if correct, player won
		if (isGuessMatch(guess)) {
			addWinner(player);
			//callbacks.forEach(c -> c.onPlayerWon(this, player, player.getNumGuesses()));
			return;
		}
		
		// if the 10th (this guess) is incorrect, player lost
		if (player.getNumGuesses() >= MAX_ATTEMPTS && isGuessMatch(guess) == false) {
			addLoser(player);
			//callbacks.forEach(c -> c.onPlayerLost(this, player, secretCode));
			return;
		}
	}
	
	public void addWinner(Player player) {
		// add the player to the winners if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && winners.contains(player) == false && losers.contains(player) == false) {
			winners.add(player);
		}
	}
	
	public void addLoser(Player player) {
		// add player to the losers if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && losers.contains(player) == false && losers.contains(player) == false) {
			losers.add(player);
		}
	}
	
	
	public boolean hasWinner(Player player) {
		return winners.contains(player);
	}
	
	
	public boolean hasLoser(Player player) {
		return losers.contains(player);
	}
	
	
	public boolean isGuessMatch(String guess) {
		return secretCode.equals(guess);
	}
	
	public Player getPlayerByName(String playerName) {
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		
		return null;
	}
	
	private void addForfeiter(Player player) {
		if (players.contains(player) && !losers.contains(player) && !winners.contains(player)) {
			forfeited.add(player);
		}
	}
	
	public boolean hasForfeited(Player player) {
		return forfeited.contains(player);
	}
	
	public void forfeit(Player player) {
		for (int i = player.getNumGuesses(); i < MAX_ATTEMPTS + 1; i++) {
			player.addGuess(null);
		}
		addForfeiter(player);
	}
	
	// this should be sorted
	public synchronized List<Player> getWinners() {
		return winners;
	}
	
	public List<Player> getLosers() {
		return losers;
	}
	
	public List<Player> getForfeiters() {
		return forfeited;
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	// end the game
	// all non-winnner players automatically lose the round
	public void end() {
		for(Player player : players) {
			if (this.hasWinner(player) == false) {
				addLoser(player);
			}
		}
		
		this.hasEnded = true;
		//callbacks.forEach(c -> c.onRoundEnded(null, this));
	}
	
	public int getNumCorrectPositions(String guess) {
		int correctPositions = 0;
		
		for (int i = 0; i < guess.length(); i++) {
			
			if (i > secretCode.length() - 1) {
				break;
			}
			
			char guessDigit = guess.charAt(i);
			char secretCodeDigit = secretCode.charAt(i);
			
			// correct position
			if (guessDigit == secretCodeDigit) {
				correctPositions += 1;
			}
		}
		
		return correctPositions;
	}
	
	public int getNumIncorrectPositions(String guess) {
		int incorrectPositions = 0;
		
		for (int i = 0; i < guess.length(); i++) {
			
			if (i > secretCode.length() - 1) {
				break;
			}
			
			char guessDigit = guess.charAt(i);
			char secretCodeDigit = secretCode.charAt(i);
			
			if (guessDigit == secretCodeDigit) {
				continue;
			}
			
			// incorrect position
			else if (secretCode.contains(new String(new char[] { guessDigit }))) {
				incorrectPositions += 1;
			}
		}
		
		return incorrectPositions;
	}
	
	
}
