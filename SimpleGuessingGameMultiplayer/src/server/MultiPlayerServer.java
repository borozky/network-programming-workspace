package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Game;
import core.GameCallback;
import core.GameCallbackImpl;
import core.GameCallbackLoggerImpl;

public class MultiPlayerServer {
	
	public static final Object LOCK = new Object();
	
	public static final int DEFAULT_PORT = 15376;
	
	private int port;
	private Game game;
	
	private List<ServerCallback> serverCallbacks = new ArrayList<>();
	private List<GameCallback> gameCallbacks = new ArrayList<>();
	
	private ServerSocket serverSocket;
	
	public MultiPlayerServer(int port, Game game) {
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

	public static void main(String[] args) throws IOException {
		
		Game game = new Game();
		ServerCallback serverCallback = new ServerCallbackImpl();
		Map<Socket, ServerProcess> processes = new ConcurrentHashMap<>();
		MultiPlayerServer multiPlayerServer = new MultiPlayerServer(DEFAULT_PORT, game);
		multiPlayerServer.addServerCallback(serverCallback);
		multiPlayerServer.start();
		ServerSocket serverSocket = multiPlayerServer.getServerSocket();
		
		game.addCallback(new GameCallbackLoggerImpl());
		game.start();
		
		do {
			Socket socket = serverSocket.accept();
			serverCallback.onClientConnected(multiPlayerServer, socket);
			GameManager gameManager = new GameManager(game);
			ServerProcess process = new ServerProcess(gameManager, socket, serverCallback);
			Thread thread = new Thread(process);
			thread.start();
			processes.put(socket, process);
		} 
		while (true);
		
		

	}

}
