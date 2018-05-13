package core;

public class GameCallbackLoggerImpl implements GameCallback {

	@Override
	public void onStart(Game game) {
		System.out.println("GAME STARTED");
	}

	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		System.out.println("Secret code generated. The code is " + secretCode);
	}

	@Override
	public void onRoundStarted(Game game, GameRound round) {
		System.out.println("New round started.");
	}

	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		System.out.printf("Player %s signed up.\n", player.getName());
	}

	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		System.out.printf("Player %s guessed %s\n", player.getName(), guess);
		
	}

	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		System.out.printf("Player %s won with %d attempts\n", player.getName(), numOfGuesses);
		
	}

	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		System.out.printf("Player %s lost the round. The secret code was %s\n", player.getName(), secretCode);
		
	}

	@Override
	public void onRoundEnded(Game game, GameRound round) {
		System.out.println("Round has ended");
		
	}

}
