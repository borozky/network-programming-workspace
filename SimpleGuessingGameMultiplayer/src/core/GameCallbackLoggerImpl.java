package core;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GameCallbackLoggerImpl implements GameCallback {
	
	private Logger logger;
	
	public GameCallbackLoggerImpl(Logger logger) {
		this.logger = logger;
	}
	

	@Override
	public void onStart(Game game) {
		GAME_STARTED("Game started");
	}
	
	private void GAME_STARTED(String message) {
		System.out.println("GAME STARTED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		SECRET_CODE_GENERATED("Generated secret code is " + secretCode);
	}
	
	private void SECRET_CODE_GENERATED(String message) {
		System.out.println("SECRET CODE GENERATED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onRoundStarted(Game game, GameRound round, Player player) {
		String response = "New round started.\nPlayers:\n";
		
		for (int i = 0; i < round.getPlayers().size(); i++) {
			response += String.format("%d. %s\n", i + 1, round.getPlayers().get(i).getName());
		}
		
		ROUND_STARTED(response);
	}
	
	private void ROUND_STARTED(String message) {
		System.out.println("ROUND STARTED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		PLAYER_SIGNED_UP(String.format("Player %s signed up.\n", player.getName()));
	}
	
	private void PLAYER_SIGNED_UP(String message) {
		System.out.println("PLAYER SIGNED UP - " + message);
		logger.log(Level.INFO, message);
	}
	

	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		GUESS_ADDED(String.format("Player %s guessed %s\n", player.getName(), guess));
	}
	
	private void GUESS_ADDED(String message) {
		System.out.println("GUESS ADDED - " +  message);
		logger.log(Level.INFO, message);
	}
	
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

	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		PLAYER_WON(String.format("Player %s won with %d attempts\n", player.getName(), numOfGuesses));
	}
	
	private void PLAYER_WON(String message) {
		System.out.println("PLAYER WON - " +  message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		PLAYER_LOST(String.format(
			"Player %s lost the round. The secret code was %s\n", player.getName(), secretCode));
	}
	
	private void PLAYER_LOST(String message) {
		System.out.println("PLAYER LOST - " +  message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onRoundEnded(Game game, GameRound round) {
		
		String response = "Round ended. \n";
		
		// Use new reference of winners collection using functional programming (lambdas)
		// using a new reference, we can sort winners 
		// without the ConcurrentModificationException 
		// when we sort the original reference
		List<Player> winners = round.getWinners().stream().filter(w -> w != null).collect(Collectors.toList());
		winners.sort(new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return o1.compareTo(o2);
			}
		});
		
		// winners
		response += "WINNERS: \n";
		if (round.getWinners().size() > 0) {
			for (int i = 0; i < winners.size(); i++) {
				response += String.format(" %d. %s (%d guesses)\n", i + 1,  winners.get(i).getName(), winners.get(i).getNumGuesses());
			}
		} else {
			response = response.concat(" * There are no winners for this round *\n");
		}
		
		// losers
		response += "LOSERS: \n";
		if (round.getLosers().size() > 0) {
			for (Player player : round.getLosers()) {
				response += String.format(" - %s\n", player.getName());
			}
		} else {
			response += " * There are no losers for this round *\n";
		}
		
		// forfeiters
		response += "FORFEITED: \n";
		if (round.getForfeiters().size() > 0) {
			for (Player player : round.getForfeiters()) {
				response += String.format("- %s\n", player.getName());
			}
		} else {
			response += " * There are no players forfeited for this round *";
		}
		
		response += "\n";
		
		
		ROUND_ENDED(response);
	}
	
	private void ROUND_ENDED(String message) {
		System.out.println("ROUND ENDED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	@Override
	public void onPlayerForfeited(Game game, GameRound round, Player player) {
		PLAYER_FORFEITED(String.format("Player %s forfeited the round.\n", player.getName()));
	}
	
	private void PLAYER_FORFEITED(String message) {
		System.out.println("PLAYER FORFEITED - " +  message);
		logger.log(Level.INFO, message);
	}
	
	@Override
	public void onPlayerQuited(Game game, Player player) {
		PLAYER_QUITED(String.format("Player %s chosen to quit the game.\n", player.getName()));
	}
	
	private void PLAYER_QUITED(String message) {
		System.out.println("PLAYER QUITED - " +  message);
		logger.log(Level.INFO, message);
	}

}
