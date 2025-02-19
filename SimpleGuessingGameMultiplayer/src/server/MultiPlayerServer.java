package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import core.Game;
import core.GameCallback;
import core.GameCallbackImpl;
import core.GameCallbackLoggerImpl;
import core.GameManager;

/**
 * Controls the logic and server operations of the Simple Guessing Game.
 * 
 * <p>This server can manage up to 6 client connection only. 
 * <p> Rules: Player has to the guess  the randomly generated 
 * secret code in order to win. 
 * 
 * @author user
 *
 */
public class MultiPlayerServer {
	
	public static final int DEFAULT_PORT = 15376;
	public static final String GAME_LOG = "game.log";
	public static final String COMMUNICATIONS_LOG = "communications.log";
	
	
	private int port;
	private Game game;
	
	// multiple callbacks needed,  not just one
	private List<ServerCallback> serverCallbacks = new ArrayList<>();
	private List<GameCallback> gameCallbacks = new ArrayList<>();
	
	// This will not be populated until you call the start() method
	private ServerSocket serverSocket;
	
	
	public MultiPlayerServer(int port, Game game) {
		this.port = port;
		this.game = game;
	}
	
	
	/**
	 * Starts the server. Behind the scenes it will launch a new instance of 
	 * ServerSocket
	 * @throws IOException
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		serverCallbacks.forEach(c -> c.onServerStarted(this, port));
	}
	
	/**
	 * Adds a new server callback
	 * @param callback
	 */
	public void addServerCallback(ServerCallback callback) {
		serverCallbacks.add(callback);
	}
	
	
	/**
	 * Get the server socket. 
	 * This will return null if start() is not called
	 * @return
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	/**
	 * Get the game instance
	 * @return
	 */
	public Game getGame() {
		return game;
	}
	
	
	/**
	 * Get game callbacks
	 * @return
	 */
	public List<GameCallback> getGameCallbacks() {
		return gameCallbacks;
	}
	
	
	/**
	 * Get server callbacks
	 * @return
	 */
	public List<ServerCallback> getServerCallbacks() {
		return serverCallbacks;
	}
	
	
	/**
	 * Close the server. 
	 * If something went wrong while closing the server, 
	 * the onException() method from {@link ServerCallback} will be fired
	 */
	public void close() {
		try {
			if (serverSocket != null) serverSocket.close();  
		} catch (IOException e) {
			serverCallbacks.forEach(c -> c.onException(null, e));
		}
	}

	
	public static void main(String[] args) throws IOException {
		
		// Create new game. 
		// Game should never be created inside other classes
		Game game = new Game();
		
		// Setup server logs
		Logger commLog = Utils.createLogger(COMMUNICATIONS_LOG, ServerCallbackImpl.class);
		ServerCallback serverCallback = new ServerCallbackImpl(commLog);
		
		// Game log
		Logger gameLog = Utils.createLogger(GAME_LOG, GameCallbackLoggerImpl.class);
		GameCallbackLoggerImpl gameCallbackLogger = new GameCallbackLoggerImpl(gameLog);
		
		// Client processes are saved here
		Map<Socket, ServerProcess> processes = new ConcurrentHashMap<>();
		
		// Create the server
		MultiPlayerServer multiPlayerServer = new MultiPlayerServer(DEFAULT_PORT, game);
		multiPlayerServer.addServerCallback(serverCallback);
		
		try {
			// start the server
			multiPlayerServer.start();
			ServerSocket serverSocket = multiPlayerServer.getServerSocket();
			game.start();

			// close the server using 'x'
			Thread background = new Thread(() -> {
				Scanner scanner = new Scanner(System.in);
				String line = "";
				System.out.println("Press 'x' to exit the server.");
				do {
					line = scanner.nextLine();
				} while (!line.equals("x"));
				
				try {
					for (ServerProcess process : processes.values()) {
						try {
							Socket socket = process.getSocket();
							socket.close();
							serverCallback.onClientDisconnected(multiPlayerServer, socket, process);
						} catch (IOException e) {
							serverCallback.onException(process, e);
						}
					}
					serverSocket.close();
				} catch (IOException e) {
					serverCallback.onException(null, e);
				}
				scanner.close();
			});
			background.start();
			
			
			do {
				// listen for new connections
				Socket socket = serverSocket.accept();
				
				// keep alive
				socket.setKeepAlive(true);
				
				serverCallback.onClientConnected(multiPlayerServer, socket);
				
				// process the game in a new thread per client connected
				ServerProcess process = new ServerProcess(game, socket, serverCallback, gameCallbackLogger);
				Thread thread = new Thread(process);
				thread.start();
				
				// save this process for future reference
				processes.put(socket, process);

			} 
			while (true);
		}
		// server operator closed the server
		catch (SocketException e) {
			System.out.println("Server closed.");
		}
		// when something wrong happens, trigger an onException() event
		catch (IOException e) {
			serverCallback.onException(null, e);
		}
		// close the server
		finally {
			multiPlayerServer.close();
		}
	}

}
