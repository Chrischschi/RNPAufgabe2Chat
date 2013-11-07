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

	public static final String SUCCESSFUL_LOGIN_MSG = "Client logged in successfully!";

	public static final String LOGIN_START_MSG = "Client trying to log in";
	
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
	public static void logClientIn(String username, InetAddress hostname) {
		System.out.println(LOGIN_START_MSG);
		addClient(new ChatUser(username, hostname)); //prüfen, ob chatuser schon in liste existiert?
		
		
		//TODO: Use this.addClient
		System.out.println(SUCCESSFUL_LOGIN_MSG);
	}
	
	public static void addClient(ChatUser newUser) {
		//vielleicht in einen synchronized-block packen? 
		// Mehrere Threads werden das hier eventuell gleichzeitig aufrufen!
		loggedInClients.add(newUser);
	}
	

}

//und startet einen neuen Arbeits-Thread für jede Verbindung,
	class ChatServerWorkThread extends java.lang.Thread {
		int threadId; //Die id des threads
		Socket workingSocket; //Der socket der dem Thread zugeteilt wurde.
		ChatServer assocSrv; //Der server, zu dem der server-thread gehört
		
		private BufferedReader inFromClient; 
		private DataOutputStream outToClient;
		
		private boolean serviceRequested = true; //bei BYE vom client wird das hier umgestellt. 
		
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
					case "NEW": doNew(messageParts); break;
					case "INFO": doInfo(messageParts); break; 
					case "BYE": doBye(messageParts); break;
					default: String reason = "Unknown Message to Server" ; sendError(reason);
					}
				}
	
			}  catch (IOException e) {
				e.printStackTrace(System.err);
			}
		}
		
		/** Methode, welche das senden von fehlernachrichten an den client 
		 * verantwortet. Fehler können bei falschen 
		 * @param reason der grund, warum der server ERROR an den client sendet
		 */
		private void sendError(String reason) throws IOException {
			writeToClientErr(reason);			
		}

		/**
		 * Methode, die ausgeführt werden soll, wenn der Server die nachricht "INFO" bekommen soll.
		 * @param messageParts die message vom client als zerlegte strings.
		 */
		private void doInfo(String[] messageParts) throws IOException {
			//Das wird defensiv programmiert, falls etwas nach dem info kommt...
			if (messageParts.length > 1) {
				//wird ein fehler an den client geschickt!
				writeToClientErr("No Parameters after INFO allowed");
			} else {
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
			
			
		}

		private void doBye(String[] messageParts) {
			// TODO Auto-generated method stub
			
		}

		private void doNew(String[] messageParts) {
			
			
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





