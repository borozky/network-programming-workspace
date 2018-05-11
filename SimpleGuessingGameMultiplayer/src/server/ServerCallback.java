package server;

import java.net.Socket;

public interface ServerCallback {
	void onServerStarted(MultiPlayerServer server, int port);
	void onClientConnected(MultiPlayerServer server, Socket socket);
	void onSendResponse(ServerProcess process, Response response);
	void onBroadcast(ServerProcess process, Response response);
	void onClientReply(ServerProcess process, String clientReply);
	void onClientDisconnected(MultiPlayerServer server, Socket socket, ServerProcess process);
	void onException(ServerProcess process, Exception e);
}
