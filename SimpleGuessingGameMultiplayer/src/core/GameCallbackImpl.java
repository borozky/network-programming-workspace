package core;

import java.io.IOException;
import java.io.ObjectOutputStream;

import server.Response;
import server.ServerProcess;

public class GameCallbackImpl implements GameCallback {
	
	private ServerProcess process;
	private ObjectOutputStream stream;
	
	public GameCallbackImpl(ServerProcess process) {
		this.process = process;
		this.stream = process.getObjectOutputStream();
	}
	
	private void reply(String message) {
		try {
			stream.writeObject(new Response(message));
		} catch (IOException e) {
			System.err.println("Sorry something went wrong while sending your message. " + e.getMessage());;
		}
	}

	@Override
	public void onStart(Game game) {
		reply("Game started");
	}

	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		System.out.printf("New secret code generated (%s)\n", secretCode);
	}

	@Override
	public void onRoundStarted(Game game, GameRound round) {
		reply("Round started");
	}

	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		reply(String.format("Player \"%s\" successfully registered.", player.getName()));
	}

	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		reply("You guessed " + guess);
	}

	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		reply("You win. Number of guesses made: " + numOfGuesses);
	}

	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		reply("You lose. Secret code is " + secretCode);
		
	}

	@Override
	public void onRoundEnded(Game game, GameRound round) {
		reply("Round ended");
	}

}
