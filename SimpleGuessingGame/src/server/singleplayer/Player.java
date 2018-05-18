package server.singleplayer;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Player {

	private String name;
	private List<String> guesses = new ArrayList<>();
	
	public Player (String name) {
		this.name = name;
	}
	
	public void addGuess(String guess) {
		guesses.add(guess);
	}
	
	public List<String> getGuesses() {
		return guesses;
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getLastGuess() {
		if (guesses.isEmpty()) {
			throw new IllegalAccessError("There are currently no guesses entered");
		}
		
		return guesses.get(guesses.size() - 1);
	}
	
	/**
	 * 
	 * @param secretCode
	 * @throws IllegalStateException If getGuesses() returns 0.
	 * @return
	 */
	public boolean isLastGuessCorrect(String secretCode) {
		int size = guesses.size();
		if (size == 0) {
			throw new IllegalStateException("There are no guesses currently made");
		}
		
		return guesses.get(size - 1).equals(secretCode);
	}
	
	
	
}
