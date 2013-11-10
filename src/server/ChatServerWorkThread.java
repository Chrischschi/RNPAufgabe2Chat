package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

// und startet einen neuen Arbeits-Thread für jede Verbindung,
class ChatServerWorkThread extends java.lang.Thread {
	private static final String UNKNOWN_MSG_TO_SRV = "Unknown Message to Server";
	private static final String ALLOWED_CHARACTERS = "([a-zA-Z]|[0-9])+";
	int threadId; // Die id des threads
	Socket workingSocket; // Der socket der dem Thread zugeteilt wurde.

	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	/*
	 * wir brauchen für unsere Serverthreads
	 * zwei zustände, einen fürs Einloggen und
	 * einen anderen für Info und Ausloggen.
	 */
	private boolean isLoggedIn = false;

	private boolean serviceRequested = true; // bei BYE vom client wird das hier
												// umgestellt.
	private ChatUser chatUser; // wird bei NEW gesetzt. Speichert die daten von
								// dem benutzer, mit dem die sitzung
								// stattfindet.

	public ChatServerWorkThread(int id, Socket sock) {
		this.threadId = id;
		this.workingSocket = sock;
	}


	public void run() {
		String messageFromClient; // das was der client einem gesendet hat.

		System.out.println("Chat Server Thread " + threadId
				+ " is running on TCP Port " + ChatServer.PORT_NUMBER
				+ " until BYE is recieved!");

		try {
			/*
			 * inputstream vom socket in inputreader und bufferedreader
			 * verpacken, weil es bequemer ist
			 */
			inFromClient = new BufferedReader(new InputStreamReader(
					workingSocket.getInputStream()));
			// Bei outToClient brauchen wir zumindest einen DataOutputStream
			outToClient = new DataOutputStream(workingSocket.getOutputStream());

			while (serviceRequested) {
				messageFromClient = readFromClient();
				String[] messageParts = messageFromClient.split(" "); // zwischen
																		// den
																		// teilen
																		// der
																		// nachricht
																		// sind
																		// leerzeichen
				String command = messageParts[0]; // der erste teil der message
													// zeigt an, ob es NEW, INFO
													// oder BYE ist.

				switch (command) {
				case ChatServer.INFO:
					doInfo();
					break;
				case ChatServer.BYE:
					doBye(messageParts);
					break;
				case ChatServer.NEW:
					doNew(messageParts, workingSocket.getInetAddress());
					break;
				default:
					sendError(UNKNOWN_MSG_TO_SRV);
				}
			}

			workingSocket.close();

		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	/**
	 * Methode, welche das senden von fehlernachrichten an den client
	 * verantwortet. Fehler können bei falschen
	 * 
	 * @param reason
	 *            der grund, warum der server ERROR an den client sendet
	 */
	private void sendError(String reason) throws IOException {
		writeToClientErr("ERROR " + reason);
		serviceRequested = false;
		logUserOut(chatUser);
	}

	private void logUserOut(ChatUser user) {
		ChatServer.logClientOut(user);
		this.chatUser = null; // über den benutzer vergessen.

	}

	/**
	 * Methode, die ausgeführt werden soll, wenn der Server die nachricht "INFO"
	 * bekommen soll.
	 * 
	 * @param messageParts
	 *            die message vom client als zerlegte strings.
	 */
	private void doInfo() throws IOException {
		StringBuilder msgBuilder = new StringBuilder("LIST ");
		// die anzahl der eingeloggten clients in die liste schreiben
		msgBuilder.append(ChatServer.loggedInClients.size());
		msgBuilder.append(" "); // Leerzeichen als trenner zwischen den
								// parametern

		for (ChatUser u : ChatServer.loggedInClients) {
			// hostname wie detailliert angeben?
			msgBuilder.append(u.hostName.getHostName() + " ");
			// userName angeben
			msgBuilder.append(u.chatName + " ");
		}
		msgBuilder.append("\n"); // nachricht mit newline abschließen

		writeToClientOut(msgBuilder.toString());

	}

	private void doBye(String[] messageParts) throws IOException {
		logUserOut(chatUser);
		writeToClientOut(ChatServer.BYE);

		serviceRequested = false;
	}

	private void doNew(String[] messageParts, InetAddress hostName)
			throws IOException {
		if (!isLoggedIn) {
			if (messageParts.length == 2) { // dann hat der username keine
											// leerzeichen, da er nicht zerlegt
											// wurde.
				String userName = messageParts[1]; // username kommt nach dem
													// NEW
				if (userName.matches(ALLOWED_CHARACTERS)) {
					ChatUser newUser = new ChatUser(userName, hostName);
					ChatServer.logClientIn(newUser); // Aufm server
					this.isLoggedIn = true; // Im serverthread
					this.saveUser(newUser);
					writeToClientOut("OK"); // laut spezifikation soll das an
											// den client gesendet werden.
				} else {
					sendError("Username invalid: contains special characters");
				}
			} else {
				sendError("Username invalid: contains spaces");
			}
		} else {
			sendError("Client already logged in");
		}

	}

	private void saveUser(ChatUser justLoggedIn) {
		this.chatUser = justLoggedIn;
	}

	private String readFromClient() throws IOException {
		String request = inFromClient.readLine();
		System.out.println("Chat Server Thread " + threadId + " detected job: "
				+ request);
		return request;
	}

	private void writeToClientErr(String reply) throws IOException {
		/*
		 * Sende den String als Antwortzeile (mit newline) zum Client, log wird
		 * auf System.err ausgegeben, für fehlernachrichten!
		 */
		outToClient.writeBytes(reply + '\n');
		System.err.println("TCP Server Thread " + threadId
				+ " has written the message: " + reply);
	}

	private void writeToClientOut(String reply) throws IOException {
		/* Sende den String als Antwortzeile (mit newline) zum Client */
		outToClient.writeBytes(reply + '\n');
		System.out.println("TCP Server Thread " + threadId
				+ " has written the message: " + reply);
	}

}