package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import server.ChatUser;

public class ChatClient {
	private static final int EXIT_FAILURE = 1; //C-Style
	
	//Konstanten, die in der Spezifikation definiert wurden
	public static final int PEER_PORT = 50001; 
	public static final int MAX_CHAT_NAME_LENGTH = 20; 
	public static final int MAX_CHAT_MSG_LENGTH = 100;
	public static final Charset REQUIRED_CHARSET = Charset.forName("UTF-8"); 
	
	/*Liste wird von ClientServerCommunicator befüllt, 
	 * mit daten aus einer INFO-nachricht vom server
	 */
	static List<ChatUser> users = new ArrayList<>(); 
	
	private static InetAddress serverIpAddressTcp; 
	
	public static void main(String[] args) {
		//Der hostname des servers wird als parameter über args übergeben.
		String serverAddressstring = args[0];
		try {
			serverIpAddressTcp = InetAddress.getByName(serverAddressstring);
		} catch (UnknownHostException shouldntHappen) {
			//in der laborumgebung sollte der host gefunden werden können.
			shouldntHappen.printStackTrace(System.err); 
			System.exit(EXIT_FAILURE); //vielleicht später lieber einen GUI-Dialog für den fehler?
		} 
		
		startServerThread(); //Thread A
		
		//TODO: Create thread for communication with server (A)
		//TODO: Create thread for broadcasting messages to peers (B) (Make it a Runnable)
		//TODO: Trigger it from a GUI event (InvocationEvent,java.awt.EventQueue!)
		//TODO: Create thread for recieving messages from peers (C)
		//TODO: last but not least, make the GUI
		
		//Thread B is started from the GUI
		
		startRecieverThread(); //Thread C
		
	}

	private static void startRecieverThread() {
		(new ClientMessageReciever(serverIpAddressTcp)).start();
	}

	private static void startServerThread() {
		(new ClientServerCommunicator()).start();
	}

}
