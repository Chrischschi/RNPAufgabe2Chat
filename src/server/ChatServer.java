package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChatServer {
	//Der Server wartet auf TCP-Verbindungsanfragen auf Port 50000
	public static final int PORT_NUMBER = 50000;

	public static final String SUCCESSFUL_LOGIN_MSG = "Client %s from host %s logged in successfully!";

	public static final String LOGIN_START_MSG = "Client %s from host %s trying to log in";
	
	//die Liste aller aktuell angemeldeten Chat-Clients (siehe ChatUser)
    public static List<ChatUser> loggedInClients = 
			Collections.synchronizedList(new ArrayList<ChatUser>()); 
	
	public static void main(String[] args) {
		ServerSocket welcomeSocket; 
		Socket connectionSocket; 
		
		int counter = 0; //Zählt die erzeugten Bearbeitungs-Threads
		
		
		try { 
			welcomeSocket = new ServerSocket(PORT_NUMBER);
		
			while(true) { //Server sollten immer laufen, vielleicht einen quit-befehl einbauen? 
				System.out.println("Chat Server: Waiting for connection on TCP Port " + PORT_NUMBER  );
			
				connectionSocket = welcomeSocket.accept();
				
				(new ChatServerWorkThread(++counter, connectionSocket)).start();
				
				
			} 
		} catch (IOException e) {
				e.printStackTrace(System.err);
		}
		
			
	}
	
	
	
	/* Wichtig für den befehl NEW aus dem Protokoll*/
	public static void logClientIn(ChatUser newUser) {
		System.out.println(String.format(LOGIN_START_MSG,newUser.chatName,newUser.hostName.getCanonicalHostName()));
		addClient(newUser); //prüfen, ob chatuser schon in liste existiert?
		
		System.out.println(String.format(SUCCESSFUL_LOGIN_MSG, newUser.chatName,newUser.hostName.getCanonicalHostName()));
	}
	
	public static void addClient(ChatUser newUser) {
		//vielleicht in einen synchronized-block packen? 
		// Mehrere Threads werden das hier eventuell gleichzeitig aufrufen!
		loggedInClients.add(newUser);
	}



	public static boolean logClientOut(ChatUser user) {
		return loggedInClients.remove(user);
		
	}
	
	

}

//und startet einen neuen Arbeits-Thread für jede Verbindung,
	class ChatServerWorkThread extends java.lang.Thread {
		int threadId; //Die id des threads
		Socket workingSocket; //Der socket der dem Thread zugeteilt wurde.
		ChatServer assocSrv; //Der server, zu dem der server-thread gehört
		
		private BufferedReader inFromClient; 
		private DataOutputStream outToClient;
		
		/* wir brauchen f�r unsere Serverthreads zwei zust�nde,
		 * einen f�rs Einloggen 
		 * einen anderen f�r Info und Ausloggen.
		 */
		private boolean isLoggedIn = false;
		
		private boolean serviceRequested = true; //bei BYE vom client wird das hier umgestellt. 
		private ChatUser chatUser; //wird bei NEW gesetzt. Speichert die daten von dem benutzer, mit dem die sitzung stattfindet.
		
		//TODO: Entscheiden, welcher der beiden konstruktoren ich brauche.
		public ChatServerWorkThread(int id, Socket sock) {
			this.threadId = id;
			this.workingSocket = sock;
		}
		
		public ChatServerWorkThread(int id, Socket sock, ChatServer associatedServer) {
			this.threadId = id;
			this.workingSocket = sock;
			this.assocSrv = associatedServer;
		}
		
		public void run() {
			String messageFromClient; //das was der client einem gesendet hat.
			
			System.out.println("Chat Server Thread " + threadId 
					+ " is running on TCP Port " + ChatServer.PORT_NUMBER 
					+ " until BYE is recieved!");
			
			try {
				/* inputstream vom socket in inputreader und bufferedreader verpacken,
				 * weil es bequemer ist */ 
				inFromClient = new BufferedReader(new InputStreamReader(
						workingSocket.getInputStream()));
				// Bei outToClient brauchen wir zumindest einen DataOutputStream
				outToClient = new DataOutputStream(workingSocket.getOutputStream());
				
				while (serviceRequested) {
					messageFromClient = readFromClient();
					String[] messageParts = messageFromClient.split(" "); // zwischen den teilen der nachricht sind leerzeichen
					String command = messageParts[0]; // der erste teil der message zeigt an, ob es NEW, INFO oder BYE ist.
					
					switch(command) {
					case "INFO": doInfo(); break; 
					case "BYE": doBye(messageParts); break;
					case "NEW": doNew(messageParts, workingSocket.getInetAddress()); break;
					default: String reason = "Unknown Message to Server" ; sendError(reason);
					}
				}
				
				
				workingSocket.close();
	
			}  catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		
		/** Methode, welche das senden von fehlernachrichten an den client 
		 * verantwortet. Fehler können bei falschen 
		 * @param reason der grund, warum der server ERROR an den client sendet
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
		 * Methode, die ausgeführt werden soll, wenn der Server die nachricht "INFO" bekommen soll.
		 * @param messageParts die message vom client als zerlegte strings.
		 */
		private void doInfo() throws IOException {
				StringBuilder msgBuilder = new StringBuilder("LIST ");
				// die anzahl der eingeloggten clients in die liste schreiben 
				msgBuilder.append(ChatServer.loggedInClients.size()); 
				msgBuilder.append(" "); //Leerzeichen als trenner zwischen den parametern
				
				for(ChatUser u : ChatServer.loggedInClients) {
					//hostname wie detailliert angeben?
					msgBuilder.append(u.hostName.getHostName() + " ");
					//userName angeben
					msgBuilder.append(u.chatName + " ");
				}
				msgBuilder.append("\n"); //nachricht mit newline abschließen
				
				writeToClientOut(msgBuilder.toString());
				
			
			
		}

		private void doBye(String[] messageParts) throws IOException {
				logUserOut(chatUser);
				writeToClientOut("BYE");
				
				serviceRequested=false;
		}

		private void doNew(String[] messageParts, InetAddress hostName) throws IOException {
			if(!isLoggedIn){ 
				if(messageParts.length == 2) { //dann hat der username keine leerzeichen, da er nicht zerlegt wurde.
				String userName = messageParts[1];
				String allowedCharacters = "([a-zA-Z]|[0-9])+";
					if(userName.matches(allowedCharacters)) {
						ChatUser newUser = new ChatUser(userName,hostName);
						ChatServer.logClientIn(newUser); //Aufm server
						this.isLoggedIn = true; // Im serverthread
						this.saveUser(newUser);
						writeToClientOut("OK"); // laut spezifikation soll das an den client gesendet werden.
					} else { sendError("Username invalid: contains special characters"); }
				} else { sendError("Username invalid: contains spaces"); }
			} else {
				sendError("Client already logged in");
			}
			
		}

		private void saveUser(ChatUser justLoggedIn) {
			this.chatUser = justLoggedIn;
		}

		private String readFromClient() throws IOException  {
			String request = inFromClient.readLine();
			System.out.println("Chat Server Thread " + threadId + " detected job: " + request);
			return request;
		}
		

		private void writeToClientErr(String reply) throws IOException {
			/* Sende den String als Antwortzeile (mit newline) zum Client, 
			 * log wird auf System.err ausgegeben, für fehlernachrichten! */
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





