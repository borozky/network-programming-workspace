package server.singleplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Game {
	
	private List<GameRound> rounds = new ArrayList<>();
	private Player player;
	private int numDigits;
	
	private GameRound currentRound;
	
	public Game() {}
	
	public List<GameRound> getRounds() {
		return rounds;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setNumSigits(int numDigits) {
		this.numDigits = numDigits;
	}
	
	public GameRound getCurrentRound() {
		return currentRound;
	}
	
	public int getCurrentRoundNumber() {
		return rounds.indexOf(getCurrentRound()) + 1;
	}
	
	public GameRound newRound(GameRoundCallback gameRoundCallback) throws Exception {
		if (gameRoundCallback == null) {
			throw new Exception("There are no game round handlers being set");
		}
		
		GameRound round = new GameRound(player, gameRoundCallback, numDigits);
		rounds.add(round);
		
		currentRound = round;
		return round;
	}
	
}
