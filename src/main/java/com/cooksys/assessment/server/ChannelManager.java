package com.cooksys.assessment.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChannelManager extends Thread {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);
	
	private ChannelData channelData;
	private Socket channelSocket;
	private Integer channelPort;

	public ChannelManager(ChannelData channelData, Integer channelPort) {
		this.channelData = channelData;
		this.channelPort = channelPort;
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		
		try {
			ss = new ServerSocket(channelPort);
		} catch (IOException e) {
			log.error("IOException in creating ServerSocket in ChannelManager:/", e);
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.error("NullPointerException in creating ServerSocket in ChannelManager:/", e);
			e.printStackTrace();
		}
		
		try {
			channelSocket = ss.accept();
		} catch (IOException e) {
			log.error("IOException in accept()ing channelSocket connection in ChannelManager:/", e);
			e.printStackTrace();
		} catch (NullPointerException e) {
			log.error("NullPointerException in accept()ing channelSocket connection in ChannelManager:/", e);
			e.printStackTrace();
		}
		
		
		
		try {
			log.info("Try-while clause is started");
			log.info("channelSocket closed = " + channelSocket.isClosed());
			ObjectMapper mapper = new ObjectMapper();
			BufferedReader channelReader = new BufferedReader(new InputStreamReader(channelSocket.getInputStream()));
//			PrintWriter channelWriter = new PrintWriter(new OutputStreamWriter(channelSocket.getOutputStream()));
			PrintWriter tempWriter = null;
			Socket clientSocket;
			
			while(!channelSocket.isClosed()){
				log.info("While loop started");
				String raw = channelReader.readLine();
				log.info("Raw string read:" + raw);
				Message message = mapper.readValue(raw, Message.class);
				log.info("Have received:" + message.toString());
				
				switch (message.getCommand()) {
					case "broadcast":
						log.info("user <{}> broadcasted message <{}>", message.getUsername(), message.getContents());
						String broadcast = mapper.writeValueAsString(message);
						Set<String> clients = channelData.getClients();
						for (String client : clients){
							clientSocket = channelData.getSocket(client);
							tempWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
							tempWriter.write(broadcast);
							tempWriter.flush();
						}
						break;
//					case "testEndCondition":
//						log.info("user <{}> triggered testEndCondition message <{}>", message.getUsername(), message.getContents());
//						if (channelData.isEmpty()){
//							channelSocket.close();
//						}
//						break;
				}
				
				
			}
			
		} catch (IOException e) {
			log.error("Failure in ChannelManager run process");
			e.printStackTrace();
		}


	}

}
