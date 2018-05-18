package server.singleplayer;

import java.util.ArrayList;
import java.util.List;

public class GameRound {
	
	public static final int MAX_ATTEMPTS = 10;
	public enum State { LOST, WON, PLAYING, STARTED }

	private Player player;
	
	private List<String> guesses = new ArrayList<>();
	
	private String secretCode;
	private GameRoundCallback callback;
	private State status;
	
	public GameRound(Player player, GameRoundCallback callback, int numDigits) {
		this.player = player;
		this.callback = callback;
		
		secretCode = Utils.generate(numDigits);
		
		setStatus(State.STARTED);
	}
	
	public State getStatus() {
		return status;
	}
	
	public int getNumDigits() {
		return secretCode.length();
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setStatus(State status) {
		switch (status) {
			case STARTED:
				callback.onStartRound(this);
				break;
			case PLAYING:
				// nothing here
				break;
			case WON:
				callback.onWin(this, secretCode);
				break;
			case LOST:
				callback.onLose(this, secretCode);
				break;
			default:
				break;
		}
		
		this.status = status;
	}

	public void addGuess(String guess) {
		if (status == State.WON || status == State.LOST) {
			return;
		}
		
		if (status == State.STARTED) {
			setStatus(State.PLAYING);
		}
		
		callback.onGuess(this, guess);
		guesses.add(guess);
		
		if (secretCode.equals(guess)) {
			callback.onCorrectGuess(this, guess);
			setStatus(State.WON);
			return;
		}
		
		callback.onIncorrectGuess(this, guess);
		if (guesses.size() >= MAX_ATTEMPTS) {
			setStatus(State.LOST);
		}
	}
	
	public int getCorrectPositions(String guess) {
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
	
	public int getIncorrectPositions(String guess) {
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
	
	public void setCallback(GameRoundCallback callback) {
		this.callback = callback;
	}
}
