package core;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Callback that logs game events into a logger.
 *  
 * <p>Because logger includes the method name of the caller of logger, each 
 * event methods will have its corresponding private method. The 
 * corresponding private methods will have upper case letters. 
 * 
 * @author user
 *
 */
public class GameCallbackLoggerImpl implements GameCallback {
	
	private Logger logger;
	
	/**
	 * Logger is required.
	 * @param logger
	 */
	public GameCallbackLoggerImpl(Logger logger) {
		this.logger = logger;
	}
	

	/**
	 * GAME STARTED event
	 */
	@Override
	public void onStart(Game game) {
		GAME_STARTED("Game started");
	}
	private void GAME_STARTED(String message) {
		System.out.println("GAME STARTED - " + message);
		logger.log(Level.INFO, message);
	}
	

	/**
	 * SECRET CODE GENERATED event
	 */
	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		SECRET_CODE_GENERATED("Generated secret code is " + secretCode);
	}
	private void SECRET_CODE_GENERATED(String message) {
		System.out.println("SECRET CODE GENERATED - " + message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * ROUND STARTED event
	 */
	@Override
	public void onRoundStarted(Game game, GameRound round, Player player) {
		ROUND_STARTED("New round started");
	}
	private void ROUND_STARTED(String message) {
		System.out.println("ROUND STARTED - " + message);
		logger.log(Level.INFO, message);
	}
	
	
	/**
	 * PLAYER SIGNED UP event
	 */
	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		PLAYER_SIGNED_UP(String.format("Player %s signed up.\n", player.getName()));
	}
	private void PLAYER_SIGNED_UP(String message) {
		System.out.println("PLAYER SIGNED UP - " + message);
		logger.log(Level.INFO, message);
	}
	
	
	/**
	 * GUESS ADDED event
	 */
	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		GUESS_ADDED(String.format("Player %s guessed %s\n", player.getName(), guess));
	}
	private void GUESS_ADDED(String message) {
		System.out.println("GUESS ADDED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	
	/**
	 * INCORRECT GUESS event
	 */
	@Override
	public void onIncorrectGuess(GameRound round, Player player, String guess) {
		INCORRECT_GUESS(String.format("Player %s guesses incorrectly\n. Correct: %s, Incorrect: %s\n", 
			player.getName(),
			round.getNumCorrectPositions(guess),
			round.getNumIncorrectPositions(guess)
		));
	}
	private void INCORRECT_GUESS(String message) {
		System.out.println("INCORRECT GUESS - " +  message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * PLAYER WON event
	 */
	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		PLAYER_WON(String.format("Player %s won with %d attempts\n", player.getName(), numOfGuesses));
	}
	private void PLAYER_WON(String message) {
		System.out.println("PLAYER WON - " +  message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * PLAYER LOST event
	 */
	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		PLAYER_LOST(String.format(
			"Player %s lost the round. The secret code was %s\n", player.getName(), secretCode));
	}
	private void PLAYER_LOST(String message) {
		System.out.println("PLAYER LOST - " +  message);
		logger.log(Level.INFO, message);
	}

	
	/**
	 * ROUND ENDED event
	 */
	@Override
	public void onRoundEnded(Game game, GameRound round) {
		ROUND_ENDED("Round ended");
	}
	private void ROUND_ENDED(String message) {
		System.out.println("ROUND ENDED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	
	/**
	 * PLAYER FORFEITED event
	 */
	@Override
	public void onPlayerForfeited(Game game, GameRound round, Player player) {
		PLAYER_FORFEITED(String.format("Player %s forfeited the round.\n", player.getName()));
	}
	private void PLAYER_FORFEITED(String message) {
		System.out.println("PLAYER FORFEITED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	
	/**
	 * PLAYER QUITED event
	 */
	@Override
	public void onPlayerQuited(Game game, Player player) {
		PLAYER_QUITED(String.format("Player %s chosen to quit the game.\n", player.getName()));
	}
	private void PLAYER_QUITED(String message) {
		System.out.println("PLAYER QUITED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	

}
