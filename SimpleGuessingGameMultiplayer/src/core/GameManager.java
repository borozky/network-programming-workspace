package core;

import java.util.ArrayList;
import java.util.List;

import core.Player.PlayerStatus;

public class GameManager {
	
	private Game game;
	private Player currentPlayer;
	private List<GameCallback> cbs = new ArrayList<>();
	
	
	public GameManager(Game game) {
		this.game = game;
	}

	
	public boolean isCurrentPlayerForfeited() {
		return currentPlayer.hasForfeited(game.getCurrentRound());
	}

	
	public Game getGame() {
		return game;
	}

	
	public void setGame(Game game) {
		this.game = game;
	}

	
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	
	public void addCallback(GameCallback cb) {
		cbs.add(cb);
	}
	
	
	public Player getFirstPlayer() {
		return game.getPlayers().get(0);
	}
	
	
	public boolean isCurrentPlayerFirst() {
		return getFirstPlayer() == currentPlayer;
	}
	
	
	public int getNumPlayers() {
		return game.getPlayers().size();
	}
	
	
	public void setCurrentPlayerName(String playerName) throws Exception {
		Player player = game.signUpPlayer(playerName);
		setCurrentPlayer(player);
		cbs.forEach(c -> c.onPlayerSignedUp(game, null, player));
	}
	
	public void setNumDigits(int numDigits) {
		if (game.getNumDigits() < Game.MIN_DIGITS || game.getNumDigits() > Game.MAX_DIGITS) {
			game.setNumDigits(numDigits);
		}
	}
	
	
	public int getNumDigits() {
		return game.getNumDigits();
	}
	
	
	public void startNextRound() throws Exception {
		GameRound round = game.getCurrentRound();
		
		if (round == null) {
			round = game.startNextRound();
			final String secretCode = round.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, secretCode));
		}
		else if (round.hasEnded()) {
			round = game.startNextRound();
			final String nextSecretCode = round.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, nextSecretCode));
		}
		
		game.getPlayers().forEach(p -> p.setStatus(PlayerStatus.STARTED));
		
		for (GameCallback c : cbs) {
			c.onRoundStarted(game, round, currentPlayer);
		}
	}
	
	
	public void addGuess(String guess) {
		GameRound round = game.getCurrentRound();
		if (guess.trim().equals("f")) {
			
			round.forfeit(currentPlayer);
			currentPlayer.setStatus(PlayerStatus.FORFEITED);
			
			cbs.forEach(c -> c.onPlayerForfeited(game, round, currentPlayer));
			checkRoundEnded(round);
			return;
		}
		
		currentPlayer.setStatus(PlayerStatus.PLAYING);
		round.addGuess(currentPlayer, guess);
		cbs.forEach(c -> c.onGuessAdded(round, currentPlayer, guess));
		
		if (! round.isGuessMatch(guess)) {
			cbs.forEach(c -> c.onIncorrectGuess(round, currentPlayer, guess));
		}
		
		if (playerWins()) {
			currentPlayer.setStatus(PlayerStatus.WON);
			cbs.forEach(c -> c.onPlayerWon(round, currentPlayer, currentPlayer.getNumGuesses()));
		}
		
		if (playerLoses()){
			currentPlayer.setStatus(PlayerStatus.LOST);
			cbs.forEach(c -> c.onPlayerLost(round, currentPlayer, round.getSecretCode()));
		}
		
		checkRoundEnded(round);
	}
	
	private void checkRoundEnded(GameRound round) {
		boolean ended = true;
		for (Player player : round.getPlayers()) {
			if (player.hasWon(round) || player.hasLost(round)) {
				continue;
			}
			
			if (player.getNumGuesses() < GameRound.MAX_ATTEMPTS) {
				ended = false;
				break;
			}
		}
		
		if (ended) {
			round.end();
		}
	}
	
	public void endCurrentRound() {
		GameRound round = game.getCurrentRound();
		if (! round.hasEnded()) {
			round.end();
		}
		
		for (GameCallback c : cbs) {
			c.onRoundEnded(game, round);
		}
	}
	
	
	public boolean playerWins() {
		return currentPlayer.hasWon(game.getCurrentRound());
	}
	
	public boolean playerLoses() {
		return currentPlayer.hasLost(game.getCurrentRound());
	}
	
	public void removeCurrentPlayer() {
		game.removePlayer(currentPlayer);
	}
	
	public void quitPlayer() {
		currentPlayer.setStatus(PlayerStatus.QUITED);
		cbs.forEach(c -> c.onPlayerQuited(game, currentPlayer));
		removeCurrentPlayer();
	}
	
	public boolean isRoundEnded() {
		return game.getCurrentRound().hasEnded();
	}
	
	public boolean isAllOtherPlayersChosenToContinueOrQuit() {
		for (Player player : game.getPlayers()) {
			if (this.currentPlayer == player) 
				continue; 
			
			switch (player.getStatus()) {
				case NOT_STARTED:
				case STARTED:
				case FORFEITED:
				case LOST:
				case PLAYING:
				case WON:
					return false;
				case QUITED:
				case CHOSEN_TO_CONTINUE:
				default: 
					break;
			}
			
			continue;
		}
		return true;
	}
	
	public void chooseToContinue() {
		currentPlayer.setStatus(PlayerStatus.CHOSEN_TO_CONTINUE);
	}
	
	public boolean isAllOtherPlayersInRoundQuited(GameRound round) {
		if (round.getPlayers().size() == 0) {
			return true;
		}
		
		for (Player player : round.getPlayers()) {
			if (player.getStatus() != PlayerStatus.QUITED) {
				return false;
			}
		}
		
		return false;
	}
	
	public void joinCurrentPlayer() {
		if (currentPlayer == null) {
			return;
		}
		
		// if player is not in current round, join player
		GameRound round = game.getCurrentRound();
		if (! round.getPlayers().contains(currentPlayer)) {
			currentPlayer.resetGuesses();
			game.getCurrentRound().addPlayer(currentPlayer);
		}
		
	}
	
	
	
	

}
