package server.singleplayer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameRoundCallbackImpl implements GameRoundCallback {
	
	private ObjectOutputStream objectOutputStream;
	private Logger logger;
	
	public GameRoundCallbackImpl(ObjectOutputStream objectOutputStream, Logger logger) {
		this.objectOutputStream = objectOutputStream;
		this.logger = logger;
	}
	
	private void sendMessage(String message) {
		try {
			objectOutputStream.writeObject(new Response(message));
		} catch (IOException e) {
			System.err.println("Sorry something went wrong while sending your message. " + e.getMessage());;
		}
	}
	
	private void log(String message) {
		System.out.println(message);
		logger.log(Level.INFO, message);
	}
	
	private void log(String format, Object... arguments) {
		log(String.format(format, arguments));
	}
	
	private void log(Exception e) {
		System.err.println(e.getMessage());
		logger.log(Level.SEVERE, e.getMessage(), e);
	}

	@Override
	public void onStartRound(GameRound round) {
		sendMessage("Round started. Guess the " + round.getNumDigits() + "-digit number");
		log("ROUND STARTED");
	}

	@Override
	public void onGuess(GameRound round, String guess) {
		sendMessage("You guessed: " + guess);
		log("PLAYER GUESSED: Player '%s' guessed %s", round.getPlayer().getName(), guess);
	}

	@Override
	public void onCorrectGuess(GameRound round, String guess) {
		sendMessage("Correct guess. Number of guesses: " + round.getGuesses().size());
		
		log("CORRECT GUESS: Player '%s' guessed %s correctly using %d attempts.", 
			round.getPlayer().getName(), 
			guess, 
			round.getPlayer().getGuesses().size()
		);
	}

	@Override
	public void onIncorrectGuess(GameRound round, String guess) {
		sendMessage(String.format("Incorrect guess. Correct: %s, Incorrect: %s\n", 
			round.getCorrectPositions(guess),
			round.getIncorrectPositions(guess)
		));
		
		log("INCORRECT GUESS: Player '%s' made incorrect guess (%d correct, %d incorrect).",
			round.getPlayer().getName(),
			round.getCorrectPositions(guess),
			round.getIncorrectPositions(guess)
		);
	}

	@Override
	public void onLose(GameRound round, String secretCode) {
		sendMessage("You lose. Secret code is " + secretCode);
		
		log("PLAYER LOST: Player '%s' lost the round. The secret code was '%s'", 
			round.getPlayer().getName(), 
			secretCode
		);
	}

	@Override
	public void onWin(GameRound round, String secretCode) {
		sendMessage("You win. Number of attempts: " + round.getGuesses().size());
		
		log("PLAYER WON: Player '%s' has won the round with %d attempts", 
			round.getPlayer().getName(), 
			round.getGuesses().size()
		);
	}
	
	@Override
	public void onForfeit(GameRound round) {
		sendMessage("You have chosen to forfeit.");
		
		log("PLAYER FORFEITED: Player '%s' has forfeited the round ", 
			round.getPlayer().getName()
		);
	}

}
