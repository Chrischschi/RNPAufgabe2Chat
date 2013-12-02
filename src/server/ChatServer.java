package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
	//Konstanten, die von ChatClient und dem ChatServerWorkThread benutzt werden.
	public static final String NEW = "NEW";

	public static final String BYE = "BYE";

	public static final String INFO = "INFO";
	
	// Der Server wartet auf TCP-Verbindungsanfragen auf Port 50000
	public static final int PORT_NUMBER = 50000;

	public static final String SUCCESSFUL_LOGIN_MSG = "Client %s from host %s logged in successfully!";

	public static final String LOGIN_START_MSG = "Client %s from host %s trying to log in";

	private static final int THREAD_LIMIT = 2;
	
	 public static final String ERROR_RESPONSE = "ERROR";
	 public static final String LIST_RESPONSE = "LIST";
	 public static final String OK_RESPONSE = "OK";

	// die Liste aller aktuell angemeldeten Chat-Clients (siehe ChatUser)
	public static List<ChatUser> loggedInClients = Collections
			.synchronizedList(new ArrayList<ChatUser>());
	
	static volatile int activeThreads = 0;

	public static void main(String[] args) {
		Socket connectionSocket;
		
		
		

		try (ServerSocket welcomeSocket = new ServerSocket(PORT_NUMBER);) {

			while (true) { // Server sollten immer laufen, vielleicht einen
							// quit-befehl einbauen?
				System.out
						.println("Chat Server: Waiting for connection on TCP Port "
								+ PORT_NUMBER);

				connectionSocket = welcomeSocket.accept();

				if(activeThreads < THREAD_LIMIT) {
					(new ChatServerWorkThread(activeThreads, connectionSocket)).start();
					activeThreads++;
				} else {
					System.err.println("Too many threads running to start a new thread!");
					connectionSocket.close(); //überflüssige ressource freigeben
					System.gc();
				} 

			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
		} 

	}

	/* Wichtig für den befehl NEW aus dem Protokoll */
	public static void logClientIn(ChatUser newUser) {
		String userName = newUser.chatName;
		String hostName = newUser.hostName.getCanonicalHostName(); // kanonischer
																	// Hostname
																	// sieht
																	// besser
																	// als aus
																	// das
																	// toString
																	// von
																	// InetAddress.
		System.out.println(String.format(LOGIN_START_MSG, userName, hostName));
		
		addClient(newUser);

		System.out.println(String.format(SUCCESSFUL_LOGIN_MSG, userName,
				hostName));
	}

	public static void addClient(ChatUser newUser) {
		// vielleicht in einen synchronized-block packen?
		// Mehrere Threads werden das hier eventuell gleichzeitig aufrufen!
		loggedInClients.add(newUser);
	}

	public static boolean logClientOut(ChatUser user) {
		return loggedInClients.remove(user);

	}



}

