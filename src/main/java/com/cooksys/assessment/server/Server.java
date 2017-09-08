package com.cooksys.assessment.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server implements Runnable {
	private Logger log = LoggerFactory.getLogger(Server.class);
	
	private int port;
	private ExecutorService executor;
	
	public Server(int port, ExecutorService executor) {
		super();
		this.port = port;
		this.executor = executor;
	}

	public void run() {
		log.info("server started");
		ServerSocket ss=null;
		ChannelData channelData = new ChannelData();
		
		try {
			ss = new ServerSocket(this.port);
			while (true) {
				log.info("Waiting for client connections...");
				Socket socket = ss.accept();
				log.info("Connected to Socket at:" + socket.getRemoteSocketAddress().toString());
				ClientHandler handler = new ClientHandler(socket, channelData);
				executor.execute(handler);
			}
		} catch (IOException e) {
			log.error("Something went wrong in Server while connecting to new Clients :/", e);
		}
	}

}
