package coretests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.*;

public class GameRoundTests {

	Game game;
	GameCallback callback;
	GameRound round;
	List<Player> players;
	int digits;

	@Before
	public void setUp() throws Exception {
		callback = new GameRoundTests_GameCallbackMock();
		game = new Game();
		game.start();
		digits = game.getRandomNum(3, 8);
		game.setNumDigits(digits);
		
		game.signUpPlayer("Player 1");
		game.signUpPlayer("Player 2");
		game.signUpPlayer("Player 3");
		game.signUpPlayer("Player 4");
		game.signUpPlayer("Player 5");
		game.signUpPlayer("Player 6");
		
		round = game.startNextRound();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_add_guess_updates_players_num_guesses() {
		String secretCode = round.getSecretCode();
		Player player = round.getPlayerByName("Player 1");
		
		// if secret code is 12345, when the user may only type 1234;
		String guess = secretCode.substring(0, secretCode.length() - 1);
		
		// invalid guess
		round.addGuess(player, guess);
		
		assertEquals(player.getGuesses().size(), 1);
	}
	
	@Test
	public void test_players_that_add_9_incorrect_guesses_neither_win_nor_lose() {
		String secretCode = round.getSecretCode();
		Player player = round.getPlayerByName("Player 1");
		
		// if secret code is 12345, when the user may only type 1234;
		String guess = secretCode.substring(0, secretCode.length() - 1);
		
		// invalid guess
		for (int i = 0; i < 9; i++) {
			round.addGuess(player, guess);
		}
		
		assertFalse(player.hasWon(round));
		assertFalse(player.hasLost(round));
	}
	
	@Test
	public void test_player_that_add_10_incorrect_guesses_loses() {
		String secretCode = round.getSecretCode();
		Player player = round.getPlayerByName("Player 1");
		
		// if secret code is 12345, when the user may only type 1234;
		String guess = secretCode.substring(0, secretCode.length() - 1);
		
		// invalid guess
		for (int i = 0; i < 10; i++) {
			round.addGuess(player, guess);
		}
		
		assertTrue(player.hasLost(round));
	}
	
	@Test
	public void test_player_that_made_correct_guess_wins() {
		String secretCode = round.getSecretCode();
		Player player = round.getPlayerByName("Player 1");
		
		round.addGuess(player, secretCode);
		
		assertTrue(player.hasWon(round));
	}
}


class GameRoundTests_GameCallbackMock implements GameCallback {

	@Override
	public void onStart(Game game) {
		System.out.println("Game started");
	}

	@Override
	public void onSecretCodeCreated(Game game, String secretCode) {
		System.out.println("Secret code generated. Secret code is " + secretCode);
	}

	@Override
	public void onRoundStarted(Game game, GameRound round, Player player) {
		System.out.printf("Round started with %d players\n", round.getPlayers().size());
	}

	@Override
	public void onPlayerSignedUp(Game game, GameRound round, Player player) {
		System.out.println("Player named \"" + player.getName() + "\" added to the current round");
	}

	@Override
	public void onGuessAdded(GameRound round, Player player, String guess) {
		System.out.printf("Player \"%s\" has guessed \"%s\"\n", player.getName(), guess);
	}

	@Override
	public void onPlayerWon(GameRound round, Player player, int numOfGuesses) {
		System.out.printf("Player \"%s\" has won the round with %d attempts\n", player.getName(), numOfGuesses);
	}

	@Override
	public void onPlayerLost(GameRound round, Player player, String secretCode) {
		System.out.printf("Player \"%s\" has lost the round. The secret code was \"%s\".\n",
			player.getName(), secretCode
		);
	}

	@Override
	public void onRoundEnded(Game game, GameRound round) {
		System.out.printf("Round has ended.");
	}
	
	@Override
	public void onPlayerForfeited(Game game, GameRound round, Player player) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPlayerQuited(Game game, Player player) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onIncorrectGuess(GameRound round, Player player, String guess) {
		// TODO Auto-generated method stub
		
	}
	
}



