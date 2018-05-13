package server;

import java.util.ArrayList;
import java.util.List;

import core.Game;
import core.GameCallback;
import core.GameRound;
import core.Player;

public class GameManager {
	
	private Game game;
	private Player player;
	private List<GameCallback> cbs = new ArrayList<>();
	
	public GameManager(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void addCallback(GameCallback cb) {
		cbs.add(cb);
	}
	
	public Player firstPlayer() {
		return game.getPlayers().get(0);
	}
	
	public boolean isCurrentPlayerFirst() {
		return firstPlayer() == player;
	}
	
	public int getNumPlayers() {
		return game.getPlayers().size();
	}
	
	public void setPlayerName(String playerName) throws Exception {
		Player player = game.signUpPlayer(playerName);
		setPlayer(player);
		cbs.forEach(c -> c.onPlayerSignedUp(game, null, player));
	}
	
	public void setNumDigits(int numDigits) {
		if (game.getNumDigits() < 3 || game.getNumDigits() > 8) {
			game.setNumDigits(numDigits);
		}
	}
	
	public int getNumDigits() {
		return game.getNumDigits();
	}
	
	public void startNextRound() throws Exception {
		GameRound round = game.getCurrentRound();
		if (round == null) {
			GameRound newRound = game.startNextRound();
			final String secretCode = newRound.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, secretCode));
		}
		cbs.forEach(c -> c.onRoundStarted(game, round));
	}
	
	public void addGuess(String guess) {
		GameRound round = game.getCurrentRound();
		round.addGuess(player, guess);
		cbs.forEach(c -> c.onGuessAdded(round, player, guess));
		
		if (playerWins()) {
			cbs.forEach(c -> c.onPlayerWon(round, player, player.getNumGuesses()));
		}
		
		if (playerLoses()){
			cbs.forEach(c -> c.onPlayerLost(round, player, round.getSecretCode()));
		}
	}
	
	
	public boolean playerWins() {
		return player.hasWon(game.getCurrentRound());
	}
	
	public boolean playerLoses() {
		return player.hasLost(game.getCurrentRound());
	}
	
	public void removeCurrentPlayer() {
		game.removePlayer(player);
	}
	
	
	
	

}
