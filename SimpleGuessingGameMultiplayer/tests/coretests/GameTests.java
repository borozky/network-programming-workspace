package coretests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import core.*;

public class GameTests {

	
	Game game;
	GameCallback callback;
	
	String[] playerNames;

	@Before
	public void setUp() throws Exception {
		callback = new GameTests_GameCallbackMock();
		game = new Game();
		game.start();
		
		playerNames = new String[] {
			"Player 1",
			"Player 2",
			"Player 3",
			"Player 4",
			"Player 5",
			"Player 6"
		};
		
		for (String name : playerNames) {
			game.signUpPlayer(name);
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_create_first_round_works() throws Exception {

		game.setNumDigits(3);
		game.startNextRound();
		assertNotNull(game.getCurrentRound());
	}
	
	@Test
	public void test_create_2nd_round() throws Exception {
		game.setNumDigits(3);
		GameRound round1 = game.startNextRound();
		round1.end();
		
		GameRound round2 = game.startNextRound();
		
		assertNotEquals(round1, round2);
	}
	
	@Test
	public void test_after_creating_next_round_the_create_round_becomes_the_current_round() throws Exception {
		game.setNumDigits(3);
		GameRound round1 = game.startNextRound();
		round1.end();
		
		GameRound round2 = game.startNextRound();
		
		assertEquals(game.getCurrentRound(), round2);
	}
	
	
	@Test
	public void test_creating_next_round_ends_the_previous() throws Exception {
		game.setNumDigits(3);
		GameRound round1 = game.startNextRound();
		round1.end();
		
		game.startNextRound();
		assertTrue(round1.hasEnded());
	}
	
	@Test
	public void test_signup_6_players_passed() throws Exception {
		game = new Game();
		game.start();

		int numPlayers = 6;
		int chosenNumberOfDigits = game.getRandomNum(3, 8);
		for (int i = 0; i < numPlayers; i++) {
			game.signUpPlayer("Player " + (i + 1));
		}
		
		game.setNumDigits(chosenNumberOfDigits);
		game.startNextRound();
		
		assertTrue(game.getPlayers().size() == numPlayers);
	}
	
	
	@Test(expected=Exception.class)
	public void test_signup_7_player_fails() throws Exception {
		int numPlayers = 7;
		int chosenNumberOfDigits = game.getRandomNum(3, 8);
		game.setNumDigits(chosenNumberOfDigits);
		game.startNextRound();
		
		for (int i = 0; i < numPlayers; i++) {
			game.signUpPlayer("Player " + (i + 1));
		}
		assertTrue(game.getCurrentRound().getPlayers().size() == numPlayers);
	}

}


class GameTests_GameCallbackMock implements GameCallback {

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
