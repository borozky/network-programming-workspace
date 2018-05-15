package core;

import java.io.IOException;
import java.io.ObjectOutputStream;

import server.Response;

public class GameCallbackImpl implements GameCallback {
	
	private ObjectOutputStream stream;
	
	public GameCallbackImpl(ObjectOutputStream stream) {
		this.stream = stream;
	}
	
	private void respond(String message) {
		respond(message, Response.PRINTMESSAGE);
	}
	
	private void respond(String message, int responseType) {
		try {
			stream.writeObject(new Response(message, responseType));
		} catch (IOException e) {
			System.err.println("Sorry something went wrong while sending your message. " + e.getMessage());
		}
	}
	

	@Override
	public void onStart(Game game) {
		respond("**GAME STARTED**");
	}

	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		System.out.printf("New secret code generated (%s)\n", secretCode);
	}

	@Override
	public void onRoundStarted(Game game, GameRound round, Player player) {
		respond("ROUND STARTED");

	}

	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		respond(String.format("Player \"%s\" successfully registered.", player.getName()));
	}

	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		respond("You guessed " + guess);
	}
	
	@Override
	public void onIncorrectGuess(GameRound round, Player player, String guess) {
		respond(String.format("INCORRECT GUESS. Correct: %s, Incorrect: %s\n", 
			round.getNumCorrectPositions(guess),
			round.getNumIncorrectPositions(guess)
		));
	}
	

	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		respond("YOU WIN. Number of guesses made: " + numOfGuesses);
	}

	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		respond(String.format("You lose. Secret code is %s.", secretCode));
	}

	@Override
	public void onRoundEnded(Game game, GameRound round) {
		respond("ROUND ENDED");
	}

	@Override
	public void onPlayerForfeited(Game game, GameRound round, Player player) {
		respond("You have forfeited this round.");
	}
	
	@Override
	public void onPlayerQuited(Game game, Player player) {
		respond("You have chosen to quit the game", Response.QUIT);
	}
}