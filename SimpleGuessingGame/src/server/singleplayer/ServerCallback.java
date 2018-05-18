package server.singleplayer;

import java.net.Socket;

public interface ServerCallback {
	
	void onInitialize(Server server);
	void onBeforeClientConnected(Server server);
	void onAfterClientConnected(Server server, Socket socket);
	void onGameStarted(Server server, Game game);
	
	void onPlayerNameRequested(Server server);
	void onPlayerNameReceived(Server server, String playerName);
	void onPlayerCreated(Server server, Player player);
	
	void onCommandSent(Server server, Response command);
	void onClientReply(Server server, String clientReply);
	
	void onPlayerQuited(Server server, Player player);
	void onGameRestarted(Server server, Game game, Player player);
	void onException(Server server, Exception e);
	
	void onServerKilled(Server server, Exception e);
}
