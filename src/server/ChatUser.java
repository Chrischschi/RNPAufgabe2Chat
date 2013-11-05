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
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof ChatUser)) return false;
		ChatUser that = (ChatUser) o;
		return this.chatName.equals(that.chatName) && 
			   this.hostName.equals(that.hostName);
	}
	

}
