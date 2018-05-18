package core;

import java.util.ArrayList;
import java.util.List;

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
	 * For single player game, each round will have 1 player
	 */
	private Player player;
	
	/**
	 * Because there's only 1-to-1 relation between player and the GameRound,
	 * we can use these field to determine the outcome of the round.
	 */
	private boolean isPlayerWon = false;
	private boolean isPlayerLost = false;
	private boolean isPlayerForfeited = false;
	
	List<String> guesses = new ArrayList<>();
	private boolean hasEnded = false;
	

	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	
	// getter and setter for isPlayerWon
	
	public boolean isPlayerWon() {
		return isPlayerWon;
	}
	public void setPlayerWon(boolean isPlayerWon) {
		this.isPlayerWon = isPlayerWon;
	}


	// getter and setter for isPlayerLost
	
	public boolean isPlayerLost() {
		return isPlayerLost;
	}
	public void setPlayerLost(boolean isPlayerLost) {
		this.isPlayerLost = isPlayerLost;
	}

	
	// getter and setter for isPlayerForfeited

	public boolean isPlayerForfeited() {
		return isPlayerForfeited;
	}
	
	public void setPlayerForfeited(boolean isPlayerForfeited) {
		this.isPlayerForfeited = isPlayerForfeited;
	}


	/**
	 * Set's the player for this round. 
	 * Round can be created without participating player
	 * <p>When you set an existing player, their guesses will be removed automatically
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
		player.clearAllGuesses();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	
	public String getSecretCode() {
		return secretCode;
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	public int getNumDigits() {
		return secretCode.trim().length();
	}
	
	/**
	 * Handles how guesses will be recorded. 
	 * By adding new guess you can the state of the round (WINNING, LOSING, FORFEIT)
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
		if (player != null && player.getNumGuesses() < MAX_ATTEMPTS) {
			player.addGuess(guess);
			guesses.add(guess);
		}
		
		// if guess if correct, player won
		if (isGuessMatch(guess)) {
			setPlayerWon(true);
			return;
		}
		
		// if the 10th (this guess) is incorrect, player lost
		if (player.getNumGuesses() >= MAX_ATTEMPTS && isGuessMatch(guess) == false) {
			setPlayerLost(true);
			return;
		}
	}
	
	public boolean hasWinner(Player player) {
		return this.player == player && isPlayerWon;
	}
	
	
	public boolean hasLoser(Player player) {
		return this.player == player && isPlayerLost;
	}
	
	
	public boolean isGuessMatch(String guess) {
		return secretCode.equals(guess);
	}
	
	
	public boolean hasForfeited(Player player) {
		return this.player == player && isPlayerForfeited;
	}
	
	public void forfeit(Player player) {
		if (this.player == player) {
			setPlayerForfeited(true);
		}
	}
	
	public boolean hasEnded() {
		return hasEnded;
	}
	
	// end the game
	// If player is still playing, he/she will automatically lose the round
	/**
	 * Ends the game.
	 * By calling this method, all players who have not won this round in 
	 * this round will automatically lose.
	 */
	public void end() {
		setPlayerLost(true);
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
			
			// stop checking for extra digits if guess is shorter than the secret code
			if (i > secretCode.length() - 1) {
				break;
			}
			
			// check if digits are the same
			char guessDigit = guess.charAt(i);
			char secretCodeDigit = secretCode.charAt(i);
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
			
			// If number of digits in the guess is lower than in secret code
			// stop checking for extra digits
			if (i > secretCode.length() - 1) {
				break;
			}
			
			// check if digits are same
			char guessDigit = guess.charAt(i);
			char secretCodeDigit = secretCode.charAt(i);
			if (guessDigit == secretCodeDigit) {
				continue;
			}
			
			// check if digit exists in secretCode
			else if (secretCode.contains(new String(new char[] { guessDigit }))) {
				incorrectPositions += 1;
			}
		}
		
		return incorrectPositions;
	}
	
	
}