package core;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import server.Response;
import server.ServerCallback;
import server.ServerProcess;

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
		String response = "----------------ROUND STARTED----------------\n";
		for (Player p : round.getPlayers()) {
			response += String.format(" - %s", p.getName());
			if (p == player) {
				response += "(you)";
			}
			
			response += "\n";
		}
		
		response += "\n";
		
		respond(response);

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
		String response = "----------------ROUND ENDED----------------\n";
		
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
		
		respond(response);
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
