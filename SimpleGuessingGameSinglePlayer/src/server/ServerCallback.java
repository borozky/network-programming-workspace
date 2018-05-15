package server;

import java.net.Socket;

public interface ServerCallback {
	void onServerStarted(SinglePlayerServer server, int port);
	void onClientConnected(SinglePlayerServer server, Socket socket);
	void onSendResponse(ServerProcess process, Response response);
	void onClientReply(ServerProcess process, String clientReply);
	void onClientDisconnected(SinglePlayerServer server, Socket socket, ServerProcess process);
	void onException(ServerProcess process, Exception e);
}