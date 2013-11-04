package server;

import java.net.InetAddress;
import java.util.List;

public class ChatServer {
	//Der Server wartet auf TCP-Verbindungsanfragen auf Port 50000
	public static final int PORT_NUMBER = 50000; 
	
	//die Liste aller aktuell angemeldeten Chat-Clients (siehe ChatUser)
	private List<ChatUser> loggedInClients;
	
	
	
	//und startet einen neuen Arbeits-Thread für jede Verbindung,
	class ChatServerWorkThread extends java.lang.Thread {
		
	}
	
	public void logClientIn(String username, InetAddress hostname) {
		//TODO: Use this.addClient
	}
	
	public void addClient(ChatUser newUser) {
		//TODO: Implement 
	}
	

}



