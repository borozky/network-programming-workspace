package server.singleplayer;

import java.io.Serializable;

public class Response implements Serializable {

	private static final long serialVersionUID = -7941032880639717329L;
	
	public static final int PRINT_MESSAGE = 0;
	public static final int ENTER_INPUT = 1;
	public static final int QUIT = 2;
	
	private String message;
	private int type;
	
	public Response(String message) {
		this(message, PRINT_MESSAGE);
	}
	
	public Response(String message, int type) {
		this.type = type;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public int getType() {
		return type;
	}
}
