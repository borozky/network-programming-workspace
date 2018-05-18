package server.singleplayer;

public interface GameRoundCallback {
	void onStartRound(GameRound round);
	void onGuess(GameRound round, String guess);
	void onCorrectGuess(GameRound round, String guess);
	void onIncorrectGuess(GameRound round, String guess);
	void onLose(GameRound round, String secretCode);
	void onWin(GameRound round, String secretCode);
	void onForfeit(GameRound round);
}
