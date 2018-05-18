package server.singleplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import server.singleplayer.GameRound.State;

public class Server implements AutoCloseable {
	
	public static final int DEFAULT_PORT_NUMBER = 15376;
	
	public static final String GAMELOG_FILENAME = "game.log";
	public static final String COMMUNICATIONSLOG_FILENAME = "communications.log";
	
	public static Logger gameLogger;
	public static Logger communicationsLogger;
	
	static {
		gameLogger = Utils.createLogger(GAMELOG_FILENAME, Game.class);
		communicationsLogger = Utils.createLogger(COMMUNICATIONSLOG_FILENAME, Server.class);
	}
	
	// required fields
	private int port;
	private ServerSocket serverSocket;
	private Game game;
	private ServerCallback serverCallback;
	
	// derived fields
	private Socket clientSocket;
	private BufferedReader reader;
	private ObjectOutputStream objectOutputStream;
	private GameRound gameRound;
	private GameRoundCallback gameRoundCallback;
	
	
	public Server(int port, Game game, ServerCallback serverCallback) throws IOException {
		this.game = game;
		this.port = port;
		this.serverCallback = serverCallback;
		
		serverSocket = new ServerSocket(this.port);
		
		// on INITALIZED
		this.serverCallback.onInitialize(this);
	}
	
	public int getPort() {
		return port;
	}
	
	// listens for client connection
	public void listen() throws IOException {
		
		// BEFORE CLIENT IS CONNECTED
		serverCallback.onBeforeClientConnected(this);
		
		// Listen for connections
		clientSocket = serverSocket.accept();
		
		reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		gameRoundCallback = new GameRoundCallbackImpl(objectOutputStream, gameLogger);
		
		// CLIENT CONNECTED
		serverCallback.onAfterClientConnected(this, clientSocket);
	}
	
	
	public void startGame() throws IOException {
		// GAME STARTED
		serverCallback.onGameStarted(this, game);
		
		// Gets the player's name and returns the Player object.
		// This will wait for the client to fill in valid name
		Player player = signUpPlayer();
		game.setPlayer(player);
		
		// select number of digits
		int numDigits = chooseNumberOfDigits();
		game.setNumSigits(numDigits);
	}
	
	
	// starts a new round
	public GameRound newRound() throws IOException, Exception {
		return game.newRound(gameRoundCallback);
	}
	
	

	public Player signUpPlayer() throws IOException {
		String playerName = clientSocket.getInetAddress().getHostAddress();
		String message = String.format("Enter your name (%s): ", playerName);
		String line = null;
		
		// PLAYER NAME REQUESTED
		serverCallback.onPlayerNameRequested(this);
		
		// Ask user for input
		line = readLine(message);
		
		if (line.isEmpty() == false) {
			playerName = line;
		}
		
		serverCallback.onPlayerNameReceived(this, playerName);
		return new Player(playerName);
	}
	
	
	// ask the client how many digits it wants
	public int chooseNumberOfDigits() throws IOException {
		int min = 3;
		int max = 8;
		int numOfDigits = Utils.getRandomNum(min, max);
		
		do {
			String message = String.format("Enter number of digits for secret code (%d)", numOfDigits);
			String line = null;
			
			// Ask client for input
			line = readLine(message);
			
			if (line.isEmpty()) {
				return numOfDigits;
			}
			
			try {
				int chosenNumDigits = Integer.parseInt(line.trim());
				
				if (chosenNumDigits < min || chosenNumDigits > max) {
					showError("Number of digits cannot be less than 3 or more than 8");
					continue;
				}
				
				return chosenNumDigits;
				
			} catch (NumberFormatException e) {
				showError(line.trim() + " is not a valid number");
				continue;
			}
		}
		while (true);
	}
	
	
	public void startGameLoop(GameRound round) throws IOException {
		String guess = "";
		do {
			guess = readLine("Enter your guess: ");
			if (guess.equals("q")) {
				quit("You have chosen to quit the game");
				break;
			}
			round.addGuess(guess);
		}
		while (round.getStatus() != State.WON && round.getStatus() != State.LOST);
	}
	
	public boolean yesOrNo(String message) throws IOException  {
		String response = "";
		
		do {
			response = readLine(message);
			response = response.toLowerCase();
			
			if (response.equals("y") || response.equals("yes")) {
				return true;
			}
			else if (response.equals("n") || response.equals("no")) {
				return false;
			}
			else {
				showError(response + " is not a valid response.");
				continue;
			}
		} while (true);
	}
	
	
	public boolean shouldContinue() throws IOException {
		if (yesOrNo("Continue game (y/n)? ") == false) {
			quit("");
			return false;
		}
		
		return true;
	}
	
	
	// close
	public void close() throws IOException {
		clientSocket.close();
		reader.close();
		objectOutputStream.close();
		serverSocket.close();
	}
	

	public String readLine(String message) throws IOException {
		String line = "";
		
		objectOutputStream.writeObject(new Response(message, Response.ENTER_INPUT));
		line = reader.readLine();
		
		// CLIENT REPLIED CALLBACK
		serverCallback.onClientReply(this, line);
		
		return line.trim();
	}
	
	
	public void quit(String message) throws IOException {
		objectOutputStream.writeObject(
			new Response(message, Response.QUIT)
		);
		
		serverCallback.onPlayerQuited(this, game.getPlayer());
	}
	
	
	public void showError(String errorMessage) throws IOException {
		objectOutputStream.writeObject(
			new Response("ERROR: " + errorMessage)
		);
	}
}
