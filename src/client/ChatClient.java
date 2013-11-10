package client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

public class ChatClient {
	private static final int EXIT_FAILURE = 1; //C-Style
	
	//Konstanten, die in der Spezifikation definiert wurden
	public final int PEER_PORT = 50001; 
	public final int MAX_CHAT_NAME_LENGTH = 20; 
	public final int MAX_CHAT_MSG_LENGTH = 100;
	//kA, ob ich das charset irgendwo explizit angeben muss.
	public final Charset REQUIRED_CHARSET = Charset.forName("UTF-8"); 
	
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
		
		//TODO: Create thread for communication with server (1.)
		//TODO: Create thread for broadcasting messages to peers (Make it a Runnable)
		//TODO: Trigger it from a GUI event (InvocationEvent,java.awt.EventQueue!)
		//TODO: Create thread for recieving messages from peers 
		
		
	}

}
