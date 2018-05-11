package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameRound {
	
	private String secretCode;
	private GameCallback cb;
	
	List<Player> players = new ArrayList<>();
	List<Player> winners = new ArrayList<>();
	List<Player> losers = new ArrayList<>();
	
	List<String> guesses = new ArrayList<>();
	
	private boolean hasEnded = false;
	
	
	public GameRound(String secretCode) {
		this.secretCode = secretCode;
	}
	
	public void addPlayer(Player player) {
		player.clearAllGuesses();
		players.add(player);
	}

	public void setCallback(GameCallback cb) {
		this.cb = cb;
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
		if (players.contains(player) && player.getNumGuesses() > 10) {
			player.addGuess(guess);
			guesses.add(guess);
			cb.onGuessAdded(this, player, guess);
		}
		
		// if guess if correct, player won
		if (isGuessMatch(guess)) {
			addWinner(player);
			cb.onPlayerWon(this, player, player.getNumGuesses());
			return;
		}
		
		// if the 10th (this guess) is incorrect, player lost
		if (player.getNumGuesses() == 10 && isGuessMatch(guess) == false) {
			addLoser(player);
			cb.onPlayerLost(this, player, secretCode);
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
	
	// this should be sorted
	public List<Player> getWinners() {
		Collections.sort(winners, new Comparator<Player>() {
			@Override
			public int compare(Player player1, Player player2) {
				return player1.getNumGuesses() - player2.getNumGuesses();
			}
		});
		
		return winners;
	}
	
	public List<Player> getLosers() {
		return losers;
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
		cb.onRoundEnded(null, this);
	}
	
	
}
