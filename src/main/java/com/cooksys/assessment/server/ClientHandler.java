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
				log.info("Raw: " + raw);
				Message message = mapper.readValue(raw, Message.class);
				log.info("Message:" + message);
				log.info("message.getCommand(): " + message.getCommand());

				switch (message.getCommand()) {
					case "connect":
						log.info("user <{}> connected", message.getUsername());
						message.setContents("has connected");
						String connectionAlert = mapper.writeValueAsString(message);
						channelData.broadcast(connectionAlert);
						channelData.addUser(message.getUsername(), clientSocket);
						break;
					case "disconnect":
						log.info("user <{}> disconnected", message.getUsername());
						channelData.removeUser(message.getUsername());
						message.setContents("has disconnected");
						String disconnectionAlert = mapper.writeValueAsString(message);
						channelData.broadcast(disconnectionAlert);
						this.clientSocket.close();
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
						break;
					case "whisper":
						log.info("user <{}> ClientHandler received whisper message <{}> for <{}>", message.getUsername(), message.getContents(), message.getTarget());
						String whisperMessage = mapper.writeValueAsString(message);
						log.info("Calling whisper with message: " + whisperMessage);
						channelData.whisper(whisperMessage);
						break;
					case "users":
						log.info("user <{}> ClientHandler received user list request message >", message.getUsername());
						String userList = channelData.users();
						message.setContents(userList);
						String usersRequest = mapper.writeValueAsString(message);
						log.info("Sending users list in message: " + usersRequest);
						clientWriter.write(usersRequest);
						clientWriter.flush();						
						break;
				}
			}

		} catch (IOException e) {
			log.error("Something went wrong :/", e);
		}
	}

}
