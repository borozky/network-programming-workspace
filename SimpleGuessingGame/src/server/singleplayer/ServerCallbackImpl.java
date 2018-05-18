package server.singleplayer;

import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerCallbackImpl implements ServerCallback {
	
	private Logger logger;
	
	public ServerCallbackImpl(Logger logger) {
		this.logger = logger;
	}
	
	private void log(String message) {
		System.out.println(message);
		logger.log(Level.INFO, message);
	}
	
	private void log(String format, Object... arguments) {
		log(String.format(format, arguments));
	}
	
	private void log(Exception e) {
		System.err.println(e.getMessage());
		logger.log(Level.SEVERE, e.getMessage(), e);
	}

	@Override
	public void onInitialize(Server server) {
		log("SERVER STARTED: Server started on port " + server.getPort());
	}

	@Override
	public void onBeforeClientConnected(Server server) {
		log("WAITING FOR CONNECTIONS...");
	}

	@Override
	public void onAfterClientConnected(Server server, Socket socket) {
		log("CLIENT CONNECTED: Client %s connected", socket.getInetAddress().getHostAddress());
	}

	@Override
	public void onGameStarted(Server server, Game game) {
		log("GAME STARTED");
	}

	@Override
	public void onPlayerNameRequested(Server server) {
		log("PLAYER NAME REQUESTED");
	}

	@Override
	public void onPlayerNameReceived(Server server, String playerName) {
		log("PLAYER NAME RECEIVED: %s\n", playerName);
	}

	@Override
	public void onPlayerCreated(Server server, Player player) {
		log("PLAYER JOINED: Player named '%s' joined", player.toString());
	}

	@Override
	public void onCommandSent(Server server, Response command) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClientReply(Server server, String clientReply) {
		log("CLIENT REPLIED: %s", clientReply);
	}

	@Override
	public void onPlayerQuited(Server server, Player player) {
		log("PLAYER QUITED: Player '%s' has decided to quit", player.getName());
	}

	@Override
	public void onGameRestarted(Server server, Game game, Player player) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onException(Server server, Exception e) {
		log(e);
	}

	@Override
	public void onServerKilled(Server server, Exception e) {
		log(e);
	}

}
