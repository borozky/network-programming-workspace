package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Player entity. The state and data sent by the client will reflect on this class.
 * 
 * @author user
 *
 */
public class Player {
	
	public enum PlayerStatus {
		NOT_STARTED,
		STARTED,
		PLAYING,
		WON,
		LOST,
		FORFEITED,
		CHOSEN_TO_CONTINUE,
		QUITED
	}
	
	private String name;
	private List<String> guesses = new ArrayList<>();
	private String lastGuess = null;
	private PlayerStatus status = PlayerStatus.NOT_STARTED;
	
	public Player(String name) {
		this.name = name;
	}
	
	public void resetGuesses() {
		guesses = new ArrayList<>();
		lastGuess = null;
	}
	
	public PlayerStatus getStatus() {
		return status;
	}

	public void setStatus(PlayerStatus status) {
		this.status = status;
	}

	public String getName() {
		return name;
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	public String getLastGuess() {
		return lastGuess;
	}
	
	public void addGuess(String guess) {
		guesses.add(guess);
		lastGuess = guess;
	}
	
	public int getNumGuesses() {
		return guesses.size();
	}
	
	public void clearAllGuesses() {
		guesses.clear();
	}
	
	public boolean hasWon(GameRound round) {
		return round.hasWinner(this);
	}
	
	public boolean hasLost(GameRound round) {
		return round.hasLoser(this);
	}
	
	public boolean hasForfeited(GameRound round) {
		return round.hasForfeited(this);
	}

}

