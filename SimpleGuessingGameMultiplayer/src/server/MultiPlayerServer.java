package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.Game;
import core.GameCallback;
import core.GameCallbackImpl;

public class MultiPlayerServer {
	
	public static final int DEFAULT_PORT = 15376;
	
	private int port;
	private Game game;
	
	private ServerSocket serverSocket;
	private Map<String, ServerProcess> serverProcesses = new ConcurrentHashMap<>();
	
	private GameCallback gameCallback;
	private ServerCallback serverCallback;
	
	
	public MultiPlayerServer(int port, Game game, 
			GameCallback gameCallback, ServerCallback serverCallback) 
			throws IOException {
		
		
		
	}
	
	
	
	
	

	public static void main(String[] args) throws IOException {
		
		Map<String, ServerProcess> serverProcesses = new ConcurrentHashMap<>();
		ServerSocket ss = new ServerSocket(DEFAULT_PORT);
		GameCallback gameCallback = new GameCallbackImpl();
		Game game = new Game(gameCallback);
		ServerCallback serverCallback = new ServerCallbackImpl();
		
		do {
			Socket socket = ss.accept();
			String address = socket.getInetAddress().toString();
			ServerProcess process = new ServerProcess(game, socket, serverCallback);
			serverProcesses.put(address, process);
			process.run();
		} while (true);

	}

}
