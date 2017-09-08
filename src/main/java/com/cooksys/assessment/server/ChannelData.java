package com.cooksys.assessment.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cooksys.assessment.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChannelData {
	private Logger log = LoggerFactory.getLogger(ClientHandler.class);

	private HashMap<String, Socket> clients;			
	private ObjectMapper mapper;

	
	public ChannelData() {
		super();
		this.clients = new HashMap<String, Socket>();
		this.mapper = new ObjectMapper();
	}

	public synchronized void broadcast(String raw){
		Message message = null;
		PrintWriter tempWriter = null;
		
		try {
			message = mapper.readValue(raw, Message.class);
			String broadcastMessage = mapper.writeValueAsString(message);
			for (Socket s : clients.values()){
				tempWriter = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
				tempWriter.write(broadcastMessage);
				tempWriter.flush();
			}
		} catch (IOException e) {
			log.error("Error in ChannelData broadcast method");
			e.printStackTrace();
		}
	}
	
	public synchronized void whisper(String raw){
		Message message = null;
		PrintWriter tempWriter = null;
		Socket targetSocket = null;
		
		try {
			message = mapper.readValue(raw, Message.class);
			String target = message.getTarget();
			String whisperMessage = mapper.writeValueAsString(message);
			targetSocket = clients.get(target);
			tempWriter = new PrintWriter(new OutputStreamWriter(targetSocket.getOutputStream()));
			tempWriter.write(whisperMessage);
			tempWriter.flush();
		} catch (IOException e) {
			log.error("Error in ChannelData broadcast method");
			e.printStackTrace();
		}
	}
	
	public String users(){
		Set<String> usersSet = clients.keySet();
		String userList = "currently connected users:\n";
		
		for (String s : usersSet){
			userList = userList + "<" + s + ">\n";
		}
		return userList;
	}
	
	public void removeUser(String username){
		clients.remove(username);
	}

	public void addUser (String username, Socket socket){
		this.clients.put(username, socket);
	}
	
	public Set<String> getClients(){
		return this.clients.keySet();
	}
	
	public Socket getSocket(String username){
		return this.clients.get(username);
	}

}
