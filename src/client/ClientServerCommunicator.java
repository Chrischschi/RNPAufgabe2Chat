package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import server.ChatUser;

public class ClientServerCommunicator extends Thread {

	public static final int SERVER_PORT = 50000;

	private Socket clientSocket; // TCP-Standard-Socketklasse

	private DataOutputStream outToServer; // Ausgabestream zum Server
	private BufferedReader inFromServer; // Eingabestream vom Server

	private int delayInSeconds = 5;

	public void startJob(String userName) {
		/* Client starten. Ende, wenn quit eingegeben wurde */
		String response; // vom User übergebener String

		/* Ab Java 7: try-with-resources mit automat. close benutzen! */
		try {
			/* Socket erzeugen --> Verbindungsaufbau mit dem Server */
			clientSocket = new Socket("localhost", SERVER_PORT);

			/* Socket-Basisstreams durch spezielle Streams filtern */
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			writeToServer("NEW " + userName);
			response = readFromServer();

			if (response.equals("OK")) {
				while (ChatClient.isServiceRequested()) {
					writeToServer("INFO"); //Get Userlist from Server
					response = readFromServer();
					List<ChatUser> users = getUsers(response); //Compute Userlist String
					ChatClient.users = users; //Replace old UserList
					Thread.sleep(delayInSeconds*1000); //Wait for 5 seconds
				}
				writeToServer("BYE");
				response = readFromServer();
				if (!response.equals("BYE")) {
					throw new Exception(response);
				}
			} else {
				throw new Exception(response);
			}
			/* Socket-Streams schließen --> Verbindungsabbau */
			clientSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Connection aborted by server!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("TCP Client stopped!");
	}

	public static List<ChatUser> getUsers(String response)
			throws UnknownHostException {
		List<ChatUser> users = new ArrayList<ChatUser>();
		String[] modifiedResponse;
		int maxUserIndex; // Index of the last Hostname (Hostname-n) within the
		// Stringarray "modifiedResponse".
		// Since the last Hostname is followed by the
		// corresponding chatname, maxUserIndex will
		// modifiedResponse.length -1.

		modifiedResponse = response.split(" "); // split Response into the
		// single Parts
		maxUserIndex = modifiedResponse.length - 1; // ->see declaration
		// The Response Array Looks like this:
		// |List|NumberOfUsers|Hostname1|UserName1|...|HostnameN|UserNameN| for
		// n = NumberOfUsers and m = size of the Array
		// Index:  0   1   2   3   ... m-3   m-2   m-1  m
		// Content: LIST  Number  Host1  User1  ... Hostn-1  Usern-1  Hostn  User
		// A new User will be found every second Index with an Offset of 2 at the beginning.
		for (int i = 2; i <= maxUserIndex; i += 2) {
			InetAddress hostName = InetAddress.getByName(modifiedResponse[i]);
			String chatName = modifiedResponse[i + 1];
			ChatUser user = new ChatUser(chatName, hostName);
			users.add(user);
		}
		return users;
	}

	private void writeToServer(String request) throws IOException {
		/* Sende eine Zeile zum Server */
		outToServer.writeBytes(request + '\n');
		System.out.println("TCP Client has sent the message: " + request);
	}

	private String readFromServer() throws IOException {
		/* Lies die Antwort (reply) vom Server */
		String reply = inFromServer.readLine();
		System.out.println("TCP Client got from Server: " + reply);
		return reply;
	}

}