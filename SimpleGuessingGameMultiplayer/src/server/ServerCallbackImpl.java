package server;

import java.net.Socket;

public class ServerCallbackImpl implements ServerCallback {

	@Override
	public void onServerStarted(MultiPlayerServer server, int port) {
		System.out.println("Server started on port " + port);
	}

	@Override
	public void onClientConnected(MultiPlayerServer server, Socket socket) {
		System.out.println("Client " + socket.getInetAddress().toString() + " connected.");
	}

	@Override
	public void onSendResponse(ServerProcess process, Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBroadcast(ServerProcess process, Response response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientReply(ServerProcess process, String clientReply) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClientDisconnected(MultiPlayerServer server, Socket socket, ServerProcess process) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onException(ServerProcess process, Exception e) {
		// TODO Auto-generated method stub
		
	}
	
}
