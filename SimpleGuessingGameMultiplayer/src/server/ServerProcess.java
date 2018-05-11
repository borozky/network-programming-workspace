package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import core.Game;

public class ServerProcess extends Thread implements AutoCloseable {
	
	// required fields
	private Game game;
	private Socket socket;
	private ServerCallback cb;
	
	// derived fields
	private ObjectOutputStream objectOutputStream;
	private BufferedReader reader;
	
	public final Object LOCK = new Object();
	
	public ServerProcess(Game game, Socket socket, ServerCallback callback) throws IOException {
		this.game = game;
		this.socket = socket;
		cb = callback;
		
		objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	
	@Override
	public void run() {
		
		
		
		
		// run game loop here
		
		
	}


	@Override
	public void close() {
		try {
			if (objectOutputStream != null) objectOutputStream.close();
			if (reader != null) reader.close();
			if (socket != null) socket.close();
		} catch (IOException e) {
			cb.onException(this, e);
		}
	}

}
