package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import core.Player.PlayerStatus;

/**
 * Class that represents the game round. One game can have many rounds. 
 * 
 * @author user
 *
 */
public class GameRound {
	
	public static final int MAX_ATTEMPTS = 10;
	
	/**
	 * Store secret code here. Each round will have 1 unique secret code
	 */
	private String secretCode;
	
	
	/**
	 * For this round there will be multiple players
	 */
	List<Player> players = new ArrayList<>();
	
	List<Player> winners = new ArrayList<>();
	List<Player> losers = new ArrayList<>();
	List<Player> forfeited = new ArrayList<>();
	
	List<String> guesses = new ArrayList<>();
	
	private boolean hasEnded = false;
	
	
	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	/**
	 * Adds the player to this round. 
	 * If player has made guesses before, 
	 * those guesses will be cleared automatically
	 * 
	 * @param player
	 */
	public void addPlayer(Player player) {
		player.clearAllGuesses();
		players.add(player);
	}
	
	/**
	 * Gets the secret code
	 * 
	 * @return
	 */
	public String getSecretCode() {
		return secretCode;
	}

	
	/**
	 * Gets all the players in this round
	 * @return
	 */
	public List<Player> getPlayers() {
		return players;
	}

	
	/**
	 * Gets all the guesses made on this round
	 * @return
	 */
	public List<String> getGuesses() {
		return guesses;
	}
	
	
	/**
	 * Gets the length of the secret code;
	 * @return
	 */
	public int getNumDigits() {
		return secretCode.trim().length();
	}
	
	
	/**
	 * Adds guesses. By adding new guess, you can 
	 * change the outcome of the players for this round
	 * (eg. WINNING, LOSING, FORFEIT)
	 * 
	 * @param player
	 * @param guess
	 */
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
	
	/**
	 * Sets the player to be the one of the winners of this round
	 * @param player
	 */
	public void addWinner(Player player) {
		// add the player to the winners if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && winners.contains(player) == false && losers.contains(player) == false) {
			winners.add(player);
		}
	}
	
	/**
	 * Sets the player to be one of the losers of this round
	 * @param player
	 */
	public void addLoser(Player player) {
		// add player to the losers if
		// - player is registered in this round
		// - player has not yet won
		// - player has not yet lost
		if (players.contains(player) && losers.contains(player) == false && losers.contains(player) == false) {
			losers.add(player);
		}
	}
	
	/**
	 * Check if the player is in the list of the winners for this round.
	 * 
	 * @param player
	 * @return
	 */
	public boolean hasWinner(Player player) {
		return winners.contains(player);
	}
	
	
	/**
	 * Checks if player is in the list of the losers for this round.
	 * @param player
	 * @return
	 */
	public boolean hasLoser(Player player) {
		return losers.contains(player);
	}
	
	
	/**
	 * Checks if guess and secret code for this round is correct
	 * @param guess
	 * @return
	 */
	public boolean isGuessMatch(String guess) {
		return secretCode.equals(guess);
	}
	
	/**
	 * Queries the list of players by name
	 * @param playerName
	 * @return
	 */
	public Player getPlayerByName(String playerName) {
		for (Player player : players) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Adds the player to the list of forfeiters for this round.
	 * @param player
	 */
	private void addForfeiter(Player player) {
		if (players.contains(player) && !losers.contains(player) && !winners.contains(player)) {
			forfeited.add(player);
		}
	}
	
	
	/**
	 * Check if the player is in the list of players forfeited
	 * @param player
	 * @return
	 */
	public boolean hasForfeited(Player player) {
		return forfeited.contains(player);
	}
	
	/**
	 * Forfeit this player in this round. 
	 * Will add guesses to the player until the player has 11 guesses
	 * @param player
	 */
	public void forfeit(Player player) {
		for (int i = player.getNumGuesses(); i < MAX_ATTEMPTS + 1; i++) {
			player.addGuess("");
		}
		addForfeiter(player);
	}
	
	/**
	 * Get all winners from this round
	 * @return
	 */
	public synchronized List<Player> getWinners() {
		return winners;
	}
	
	/**
	 * Get all losers from this round
	 * @return
	 */
	public List<Player> getLosers() {
		return losers;
	}
	
	/**
	 * Get all players forfeited from this round
	 * @return
	 */
	public List<Player> getForfeiters() {
		return forfeited;
	}
	
	/**
	 * Check if this round has ended
	 * @return
	 */
	public boolean hasEnded() {
		return hasEnded;
	}
	
	/**
	 * End the game manually. All non-winner players will lose automatically.
	 */
	public void end() {
		for(Player player : players) {
			if (this.hasWinner(player) == false) {
				addLoser(player);
			}
		}
		
		this.hasEnded = true;
	}
	
	/**
	 * Matches the guess to the set secret code. 
	 * If a digits from guess is found in secret code and in the same position, the digit is in 'correct position'
	 * 
	 * @param guess
	 * @return
	 */
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
	
	/**
	 * Matches the guess to the set secret code. 
	 * If a digit from guess is found in the secret code but not in correct position, the digit is in 'incorrect position'
	 * 
	 * @param guess
	 * @return
	 */
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
