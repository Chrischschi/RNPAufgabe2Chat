package server;

import java.net.InetAddress;

public class ChatUser {
	/**
	 * jeweils Chat-Name des Benutzers und Hostname des Clients
	 */
	final String chatName; 
	
	final InetAddress hostName;
	
	public ChatUser(String chatName, InetAddress hostName) {
		this.chatName = chatName;
		this.hostName = hostName;
	}
	

}
