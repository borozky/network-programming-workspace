package server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerCallbackImpl implements ServerCallback {
	
	private Logger logger;
	public ServerCallbackImpl(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void onServerStarted(MultiPlayerServer server, int port) {
		SERVER_STARTED("Server started on port " + port);
	}
	
	private void SERVER_STARTED(String message) {
		System.out.println("SERVER STARTED - " + message);
		logger.log(Level.INFO, message);
	}
	

	@Override
	public void onClientConnected(MultiPlayerServer server, Socket socket) {
		CLIENT_CONNECTED("Client " + socket.getInetAddress().toString() + " connected.");
	}
	
	private void CLIENT_CONNECTED(String message) {
		System.out.println("CLIENT CONNECTED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onSendResponse(ServerProcess process, Response response) {
		SERVER_RESPONDED("Server: " + response.getMessage());
	}
	
	private void SERVER_RESPONDED(String message) {
		System.out.println("SERVER RESPONDED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onClientReply(ServerProcess process, String clientReply) {
		String address = process.getSocket().getInetAddress().toString();
		CLIENT_REPLIED(String.format("%s: %s\n", address, clientReply));
	}
	
	private void CLIENT_REPLIED(String message) {
		System.out.println("CLIENT REPLIED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onClientDisconnected(MultiPlayerServer server, Socket socket, ServerProcess process) {
		String address = socket.getInetAddress().toString();
		CLIENT_DISCONNECTED(String.format("CLIENT DISCONNECTED: %s disconnected.", address));
	}
	
	private void CLIENT_DISCONNECTED(String message) {
		System.out.println("CLIENT DISCONNECTED - " + message);
		logger.log(Level.INFO, message);
	}

	@Override
	public void onException(ServerProcess process, Exception e) {
		EXCEPTION_THROWN(e);
	}
	
	public void EXCEPTION_THROWN(Exception e) {
		System.err.println("EXCEPTION: " + e.getMessage());
		e.printStackTrace();
		logger.log(Level.SEVERE, e.getMessage(), e);
	}
	
}
