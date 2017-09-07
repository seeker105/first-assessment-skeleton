package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private Socket clientSocket;
	private ChannelData channelData;

	public ClientHandler(Socket clientSocket, ChannelData channelData) {
		super();
		this.clientSocket = clientSocket;
		this.channelData = channelData;
	}

	public void run() {
		try {

			ObjectMapper mapper = new ObjectMapper();
			BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter clientWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
//			BufferedReader channelReader = new BufferedReader(new InputStreamReader(channelSocket.getInputStream()));
//			PrintWriter channelWriter = new PrintWriter(new OutputStreamWriter(channelData.getOutputStream()));

			while (!clientSocket.isClosed()) {
				String raw = clientReader.readLine();
				Message message = mapper.readValue(raw, Message.class);

				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						channelData.addUser(message.getUsername(), clientSocket);
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						this.clientSocket.close();
//						message.setCommand("testEndCondition");
//						String testCommand = mapper.writeValueAsString(message);
//						channelWriter.write(testCommand);
//						channelWriter.flush();
						break;
					case "echo":
						log.info("user <{}> echoed message <{}>", message.getUsername(), message.getContents());
						String response = mapper.writeValueAsString(message);
						clientWriter.write(response);
						clientWriter.flush();
						break;
					case "broadcast":
						log.info("user <{}> ClientHandler received broadcast message <{}>", message.getUsername(), message.getContents());
						String broadcastMessage = mapper.writeValueAsString(message);
						log.info("Calling broadcast with message: "  + broadcastMessage);
						channelData.broadcast(broadcastMessage);
//						log.info("Ready to write:" + broadcast);
//						channelWriter.write(broadcast);
//						log.info("Have written:" + broadcast);
//						channelWriter.flush();
//						log.info("Have flushed:" + broadcast);
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
