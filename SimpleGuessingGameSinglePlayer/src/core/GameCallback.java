package core;

/**
 * Series of game events. 
 * <p>Methods of this interface will be called in by the core.GameManager class
 * 
 * @author user
 */
public interface GameCallback {
	
	void onStart(Game game);
	void onSecretCodeCreated(Game game, String secretCode);
	void onRoundStarted(Game game, GameRound round, Player player);
	void onPlayerSignedUp(Game game, GameRound round, Player player);
	void onGuessAdded(GameRound round, Player player, String guess);
	void onIncorrectGuess(GameRound round, Player player, String guess);
	void onPlayerWon(GameRound round, Player player, int numOfGuesses);
	void onPlayerLost(GameRound round, Player player, String secretCode);
	void onRoundEnded(Game game, GameRound round);
	void onPlayerForfeited(Game game, GameRound round, Player player);
	void onPlayerQuited(Game game, Player player);

}
