package com.cooksys.assessment.model;

import java.net.Socket;

public class User {
	
	private Socket socket;
	private String username;
	
	public User(Socket socket) {
		this.socket = socket;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}
}
