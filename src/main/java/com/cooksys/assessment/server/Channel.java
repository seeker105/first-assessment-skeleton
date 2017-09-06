package com.cooksys.assessment.server;

import java.net.Socket;
import java.util.HashMap;

public class Channel implements Runnable {
	
	private HashMap<String, Socket> sockets;

	public Channel() {
		sockets = new HashMap<String, Socket>();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

}
