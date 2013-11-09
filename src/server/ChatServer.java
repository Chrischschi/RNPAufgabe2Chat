package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
	// Der Server wartet auf TCP-Verbindungsanfragen auf Port 50000
	public static final int PORT_NUMBER = 50000;

	public static final String SUCCESSFUL_LOGIN_MSG = "Client %s from host %s logged in successfully!";

	public static final String LOGIN_START_MSG = "Client %s from host %s trying to log in";

	// die Liste aller aktuell angemeldeten Chat-Clients (siehe ChatUser)
	public static List<ChatUser> loggedInClients = Collections
			.synchronizedList(new ArrayList<ChatUser>());

	public static void main(String[] args) {
		Socket connectionSocket;

		int counter = 0; // Zählt die erzeugten Bearbeitungs-Threads

		try (ServerSocket welcomeSocket = new ServerSocket(PORT_NUMBER);) {

			while (true) { // Server sollten immer laufen, vielleicht einen
							// quit-befehl einbauen?
				System.out
						.println("Chat Server: Waiting for connection on TCP Port "
								+ PORT_NUMBER);

				connectionSocket = welcomeSocket.accept();

				(new ChatServerWorkThread(++counter, connectionSocket)).start();

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

