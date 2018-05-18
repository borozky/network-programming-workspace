package core;

import java.util.ArrayList;
import java.util.List;

import core.Player.PlayerStatus;

/**
 * Classes that bridges game object to all callbacks. 
 * You can say this is the brains of the game. 
 * Each connection to the client will create a new instance of the game manager.
 * Game manager should never be shared on all clients (1 game manager per client)
 * 
 * @author user
 *
 */
public class GameManager {
	
	// To bridge the game with the callbacks, you will need the game object and the callbacks
	private Game game;
	private List<GameCallback> cbs = new ArrayList<>();

	/**
	 * Current player info is saved here since 
	 * there will be only 1 client per instance of GameManager
	 */
	private Player currentPlayer;
	
	
	/**
	 * Game is required. GameManager should still work even without callbacks
	 * @param game
	 */
	public GameManager(Game game) {
		this.game = game;
	}


	/**
	 * Gets the instance of the game
	 * @return
	 */
	public Game getGame() {
		return game;
	}
	
	/**
	 * Modifies the game this class uses
	 * @param game
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Gets the current player. This may return null if the round has not yet started. 
	 * @return
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	/**
	 * Sets the current player
	 * @param player
	 */
	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}
	
	
	/**
	 * Add a callback
	 * @param cb
	 */
	public void addCallback(GameCallback cb) {
		cbs.add(cb);
	}
	
	
	/**
	 * Gets the first player registered in the {@link Game}
	 * 
	 * @return
	 */
	public Player getFirstPlayer() {
		return game.getPlayers().get(0);
	}
	
	
	/**
	 * Checks if the current player of this class is the first player in the game
	 * 
	 * @return
	 */
	public boolean isCurrentPlayerFirst() {
		return getFirstPlayer() == currentPlayer;
	}
	
	/**
	 * Gets number of players in the game
	 * @return
	 */
	public int getNumPlayers() {
		return game.getPlayers().size();
	}
	
	
	/**
	 * Registers the player name to the game.
	 * This will set off a onPlayerSignedUp() trigger (see {@link GameCallback})
	 * 
	 * @param playerName
	 * @throws Exception
	 */
	public void setCurrentPlayerName(String playerName) throws Exception {
		Player player = game.signUpPlayer(playerName);
		setCurrentPlayer(player);
		cbs.forEach(c -> c.onPlayerSignedUp(game, null, player));
	}
	
	/**
	 * Modifies the number of digits in the game if it is currently not set
	 * 
	 * @param numDigits
	 */
	public void setNumDigits(int numDigits) {
		if (game.getNumDigits() < Game.MIN_DIGITS || game.getNumDigits() > Game.MAX_DIGITS) {
			game.setNumDigits(numDigits);
		}
	}
	
	
	/**
	 * Gets the number of digits in the game
	 * @return
	 */
	public int getNumDigits() {
		return game.getNumDigits();
	}

	/**
	 * Check if the player managed by this class has forfeited
	 * @return
	 */
	public boolean isCurrentPlayerForfeited() {
		return currentPlayer.hasForfeited(game.getCurrentRound());
	}
	
	/**
	 * Starts a new round, if there are no previous round, 
	 * or if the previous round has ended.
	 * 
	 * <p>Each time a new round is created, a new secret code
	 * will be assigned to the new round and callback 
	 * onSecretCodeCreated() is invoked
	 * 
	 * <p>Also when you call this method, onRoundStarted() callback will be invoked
	 * whether or not a new round is generated.
	 * 
	 * @throws Exception
	 */
	public void startNextRound() throws Exception {
		GameRound round = game.getCurrentRound();
		
		// first round
		if (round == null) {
			round = game.startNextRound();
			final String secretCode = round.getSecretCode();
			cbs.forEach(c -> c.onSecretCodeCreated(game, secretCode));
		}
		
		// previous round has ended
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
	
	
	/**
	 * Add player's guess to the round. The outcome of the round will be determined by this method.
	 * <p>If player has added a guess, a onGuessAdded() event is fired. 
	 * However if player forfeits by pressing "f", the onPlayerForfeited() 
	 * event is called instead without calling the onGuessAdded(). 
	 * <p>If guess is not correct, the onIncorrectGuess() is fired, 
	 * but it is correct, the player wins and the onPlayerWon() event will be fired.
	 * <p>After the player made its 10th incorrect guess, the player 
	 * automatically loses and the onPlayerLost() is fired.
	 * <p>Every time a guess is added, this method will check if 
	 * the guess made has caused the round to be ended (eg. if all players have lost, won or forfeited).
	 * 
	 * @param guess
	 */
	public void addGuess(String guess) {
		GameRound round = game.getCurrentRound();
		
		// player forfeits
		if (guess.trim().equals("f")) {
			
			round.forfeit(currentPlayer);
			currentPlayer.setStatus(PlayerStatus.FORFEITED);
			
			cbs.forEach(c -> c.onPlayerForfeited(game, round, currentPlayer));
			checkRoundEnded(round);
			return;
		}
		
		// add guess
		currentPlayer.setStatus(PlayerStatus.PLAYING);
		round.addGuess(currentPlayer, guess);
		cbs.forEach(c -> c.onGuessAdded(round, currentPlayer, guess));
		
		// check outcome
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
	
	/**
	 * Helper method that checks if the current round needs to be ended
	 * 
	 * @param round
	 */
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
	
	
	/**
	 * Force the round to end. In turn, the event onRoundEnded() will be fired
	 */
	public void endCurrentRound() {
		GameRound round = game.getCurrentRound();
		if (! round.hasEnded()) {
			round.end();
		}
		
		for (GameCallback c : cbs) {
			c.onRoundEnded(game, round);
		}
	}
	
	
	/**
	 * Convenience method to check if current player wins the current round
	 * @return
	 */
	public boolean playerWins() {
		return currentPlayer.hasWon(game.getCurrentRound());
	}
	
	/**
	 * Convenience method to check if current player loses the current round
	 * @return
	 */
	public boolean playerLoses() {
		return currentPlayer.hasLost(game.getCurrentRound());
	}
	
	public void removeCurrentPlayer() {
		game.removePlayer(currentPlayer);
	}
	
	/**
	 * Player has chosen to quit. This fires the onPlayerQuited() event
	 */
	public void quitPlayer() {
		currentPlayer.setStatus(PlayerStatus.QUITED);
		cbs.forEach(c -> c.onPlayerQuited(game, currentPlayer));
		removeCurrentPlayer();
	}
	
	/**
	 * Check if current game round has ended
	 * @return
	 */
	public boolean isRoundEnded() {
		return game.getCurrentRound().hasEnded();
	}
	
	/**
	 * Convenience method to check if player decided to quit or continue. 
	 * If players have not started or still 
	 * adding their guesses, this returns false.
	 * 
	 * @return
	 */
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
	
	
	
	/**
	 * Check if player status has CHOSEN_TO_CONTINUE
	 */
	public void chooseToContinue() {
		currentPlayer.setStatus(PlayerStatus.CHOSEN_TO_CONTINUE);
	}
	
	/**
	 * Convenience method to check if other players have quit the round
	 * @param round
	 * @return
	 */
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
	
	/**
	 * Adds the current player to the current round.
	 */
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
