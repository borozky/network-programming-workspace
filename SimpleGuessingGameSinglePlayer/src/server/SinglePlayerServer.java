package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import core.Game;
import core.GameCallback;
import core.GameCallbackLoggerImpl;

public class SinglePlayerServer {
	
	public static final int DEFAULT_PORT = 15376;
	public static final String GAME_LOG = "game.log";
	public static final String COMMUNICATIONS_LOG = "communications.log";
	
	private int port;
	private Game game;
	private List<ServerCallback> serverCallbacks = new ArrayList<>();
	private List<GameCallback> gameCallbacks = new ArrayList<>();
	private ServerSocket serverSocket;
	
	public SinglePlayerServer(int port, Game game) {
		this.port = port;
		this.game = game;
	}
	
	public void start() throws IOException {
		serverSocket = new ServerSocket(port);
		serverCallbacks.forEach(c -> c.onServerStarted(this, port));
	}
	
	public void addServerCallback(ServerCallback callback) {
		serverCallbacks.add(callback);
	}
	
	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public Game getGame() {
		return game;
	}
	
	public List<GameCallback> getGameCallbacks() {
		return gameCallbacks;
	}
	
	public List<ServerCallback> getServerCallbacks() {
		return serverCallbacks;
	}
	
	public void close() {
		try {
			if (serverSocket != null) serverSocket.close();  
		} catch (IOException e) {
			serverCallbacks.forEach(c -> c.onException(null, e));
		}
	}
	
	
	public static void main(String[] args) {
		Game game = new Game();
		
		Logger commLog = Utils.createLogger(COMMUNICATIONS_LOG, ServerCallbackImpl.class);
		ServerCallback serverCallback = new ServerCallbackImpl(commLog);
		
		Logger gameLog = Utils.createLogger(GAME_LOG, GameCallbackLoggerImpl.class);
		GameCallbackLoggerImpl gameCallbackLogger = new GameCallbackLoggerImpl(gameLog);
		
		
		SinglePlayerServer singlePlayerServer = new SinglePlayerServer(DEFAULT_PORT, game);
		
		singlePlayerServer.addServerCallback(serverCallback);
		
		try {
			singlePlayerServer.start();
			
			ServerSocket serverSocket = singlePlayerServer.getServerSocket();
			game.start();
			
			do {
				Socket socket = serverSocket.accept();
				serverCallback.onClientConnected(singlePlayerServer, socket);
				ServerProcess process = new ServerProcess(game, socket, serverCallback, gameCallbackLogger);
				process.run();
			} while (true);
			
		}
		catch (IOException e) {
			serverCallback.onException(null, e);
		}
		finally {
			singlePlayerServer.close();
		}
		
	}

}
