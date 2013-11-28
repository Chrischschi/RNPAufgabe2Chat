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
	/*
	 * Regulärer ausdruck für String.split, Aus Hübners spezifikation:
	 * "Message-Name und Parameter werden durch mindestens ein Leerzeichen
	 * getrennt.", Also mindestens ein leerzeichen, danach beliebig viele, das
	 * ist " +".
	 */
	private static final String ONE_OR_MORE_SPACES = " +";

	public static final int SERVER_PORT = 50000;

	private Socket clientSocket; // TCP-Standard-Socketklasse

	private DataOutputStream outToServer; // Ausgabestream zum Server
	private BufferedReader inFromServer; // Eingabestream vom Server

	private static final int DELAY_IN_SECONDS = 5;

	InetAddress serverHostAddress;

	boolean loggedIn = false;

	public ClientServerCommunicator(InetAddress serverHostAddress) {
		this.serverHostAddress = serverHostAddress;
	}

	/**
	 * Loggt den benutzer ein und erfüllt somit den zweck von den zeilen 79 und
	 * 80 der methode ClientServerCommunicator#run() Dies wird ausgelagert,
	 * damit die anmeldung an den server (NEW) und die abfrage der liste (INFO)
	 * von einander getrennt ausgeführt werden können. Ein problem dabei ist die
	 * tatsache, dass sich jedoch das Ein- loggen (der "NEW"-Schritt) und das
	 * abholen der user-liste vom server (der "INFO"-Schritt") die gleiche
	 * verbindung/den gleichen socket teilen müssen. Dies wird dadurch behoben,
	 * dass der ClientServerCom municator Thread einfach den von logUserIn
	 * benutzten socket bekommt
	 * 
	 * @param logInName
	 *            der name mit dem sich der benutzer einloggen will sollte keine
	 *            leerzeichen und sonderzeichen enthalten.
	 * @return der Socket, der die verbindung darstellt, mit welcher sich der
	 *         client beim server eingelogt hat. Falls die verbindung nicht ge
	 *         klappt hat, wird null zurückgegeben.
	 */
	boolean tryLogUserIn(String userName) {
		String response = "";
		try {
			clientSocket = new Socket(serverHostAddress, SERVER_PORT);

			/* Socket-Basisstreams durch spezielle Streams filtern */
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			inFromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			writeToServer("NEW " + userName);
			response = readFromServer();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("Connection aborted by server!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		loggedIn = response.equals("OK");

		return loggedIn;

	}

	public void run() {
		if (loggedIn) {
			/* Client starten. Ende, wenn quit eingegeben wurde */
			String response; // vom User übergebener String
			try {
				while (ChatClient.isServiceRequested()) {
					writeToServer("INFO"); // Get Userlist from Server
					response = readFromServer();
					List<ChatUser> users = getUsers(response); // Compute
																// Userlist
																// String
					ChatClient.users.clear();
					ChatClient.users.addAll(users);
                                        
                                        updateGUI(users);
                                        updateClientSenderThread(users);
                                        
                                        
                                        
					Thread.sleep(DELAY_IN_SECONDS * 1000); // Wait for delayInSeconds seconds
				}
				writeToServer("BYE");
				response = readFromServer();
				if (!response.equals("BYE")) {
					throw new Exception(response);
				}

				// Ab hier bereitet sich der thread nur noch auf sein Ende vor.
				closeResources();

			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("Connection aborted by server!");
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println("TCP Client stopped!");

		} else {
			System.out.println("not logged in");
		}

	}

	private void closeResources() throws IOException {
		outToServer.close();
		inFromServer.close();
		clientSocket.close();
	}

	public static List<ChatUser> getUsers(String response) {
		List<ChatUser> users = new ArrayList<ChatUser>();
		String[] modifiedResponse;
		int maxUserIndex; // Index of the last Hostname (Hostname-n) within the
		// Stringarray "modifiedResponse".
		// Since the last Hostname is followed by the
		// corresponding chatname, maxUserIndex will
		// modifiedResponse.length -1.

		modifiedResponse = response.split(ONE_OR_MORE_SPACES); // split Response
																// into the
		// single Parts
		maxUserIndex = modifiedResponse.length - 1; // ->see declaration
		// The Response Array Looks like this:
		// |List|NumberOfUsers|Hostname1|UserName1|...|HostnameN|UserNameN| for
		// n = NumberOfUsers and m = size of the Array
		// Index: 0 1 2 3 ... m-3 m-2 m-1 m
		// Content: LIST Number Host1 User1 ... Hostn-1 Usern-1 Hostn User
		// A new User will be found every second Index with an Offset of 2 at
		// the beginning.
		for (int i = 2; i <= maxUserIndex; i += 2) {
			try {
				InetAddress hostName = InetAddress
						.getByName(modifiedResponse[i]);
				String chatName = modifiedResponse[i + 1];
				ChatUser user = new ChatUser(chatName, hostName);
				users.add(user);
			} catch (UnknownHostException e) {
				System.err.println("Could not find Host " + modifiedResponse[i]
						+ "\nUser " + modifiedResponse[i + 1]
						+ " will be skipped\n" + e.getMessage());
			}
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

    private void updateGUI(List<ChatUser> users) {
        
    }

    private void updateClientSenderThread(List<ChatUser> users) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}