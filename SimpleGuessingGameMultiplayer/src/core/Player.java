package core;

import java.util.ArrayList;
import java.util.List;

public class Player {
	
	String name;
	List<String> guesses = new ArrayList<>();
	String lastGuess = null;
	
	public Player(String name) {
		this.name = name;
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

}
