package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the round. 
 * 
 * @author user
 *
 */
public class GameRound {
	
	public static final int MAX_ATTEMPTS = 10;
	
	private String secretCode;
	
	private Player player;
	private boolean isPlayerWon = false;
	private boolean isPlayerLost = false;
	private boolean isPlayerForfeited = false;
	
	List<String> guesses = new ArrayList<>();
	private boolean hasEnded = false;
	
	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	public boolean isPlayerWon() {
		return isPlayerWon;
	}

	public void setPlayerWon(boolean isPlayerWon) {
		this.isPlayerWon = isPlayerWon;
	}


	public boolean isPlayerLost() {
		return isPlayerLost;
	}

	public void setPlayerLost(boolean isPlayerLost) {
		this.isPlayerLost = isPlayerLost;
	}


	public boolean isPlayerForfeited() {
		return isPlayerForfeited;
	}
	
	public void setPlayerForfeited(boolean isPlayerForfeited) {
		this.isPlayerForfeited = isPlayerForfeited;
	}


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
	// all non-winnner players automatically lose the round
	public void end() {
		setPlayerLost(true);
		this.hasEnded = true;
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