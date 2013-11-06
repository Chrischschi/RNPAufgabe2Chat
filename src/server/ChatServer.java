package server;

import java.net.InetAddress;
import java.util.List;

public class ChatServer {
	//Der Server wartet auf TCP-Verbindungsanfragen auf Port 50000
	public static final int PORT_NUMBER = 50000;

	public static final String SUCCESSFUL_LOGIN_MSG = "Client logged in successfully!";
	
	//die Liste aller aktuell angemeldeten Chat-Clients (siehe ChatUser)
	public List<ChatUser> loggedInClients; //TODO: später private machen
	
	
	
	//und startet einen neuen Arbeits-Thread für jede Verbindung,
	class ChatServerWorkThread extends java.lang.Thread {
		
	}
	
	/* Wichtig für den befehl NEW aus dem Protokoll*/
	public void logClientIn(String username, InetAddress hostname) {
		//TODO: Use this.addClient
		System.out.println(SUCCESSFUL_LOGIN_MSG);
	}
	
	public void addClient(ChatUser newUser) {
		//vielleicht in einen synchronized-block packen? 
		// Mehrere Threads werden das hier eventuell gleichzeitig aufrufen!
		loggedInClients.add(newUser);
	}
	

}



