package client;

import client.gui.ChatClientGUI;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import server.ChatUser;

public class ChatClient {
	private static final int EXIT_FAILURE = 1; //C-Style
	
	//Konstanten, die in der Spezifikation definiert wurden
	public static final int PEER_PORT = 50001; 
	public static final int MAX_CHAT_NAME_LENGTH = 20; 
	public static final int MAX_CHAT_MSG_LENGTH = 100;
	public static final Charset REQUIRED_CHARSET = Charset.forName("UTF-8"); 
	
	//Abhängige Konstanten 
	public static int DATAGRAM_SIZE = //+1 am ende für das newline
			MAX_CHAT_NAME_LENGTH + ClientSenderThread.COLON_AND_SPACE  + MAX_CHAT_MSG_LENGTH + 1; 
	
	
	/*Liste wird von ClientServerCommunicator befüllt, 
	 * mit daten aus einer INFO-nachricht vom server
	 */
	public static List<ChatUser> users = new ArrayList<>();
	
	/*
	 * ein puffer, welcher die von ClientMessageReciever gelesenen
	 * nachrichten zwischenspeichert, damit sie von der gui konsumiert
	 * werden können. Ich habe ehrlich gesagt keine ahnung, wie ich 
	 * das JTextField befüllen soll.
	 */
	static StringBuffer chatMessages = new StringBuffer(DATAGRAM_SIZE * 10); 
	//TODO Entscheiden, ob ich diesen StringBuffer gebrauchen kann.
	
	private static boolean serviceRequested = true;

	public static boolean isServiceRequested() {
	  return serviceRequested;
	}
	
	private static String logInName; //der name, mit dem man sich beim server einloggt
	private static InetAddress serverIpAddressTcp; //addresse des servers
	
	public static void main(String[] args) {
               
		/*Der hostname des servers wird in einem dialog vor start der
                  eigentlichen GUI eingegeben.
                */  
                
		String serverAddressstring = JOptionPane.showInputDialog("Bitte hostnamen eingeben");
		try {
			serverIpAddressTcp = InetAddress.getByName(serverAddressstring);
		} catch (UnknownHostException shouldntHappen) {
			//in der laborumgebung sollte der host gefunden werden können.
                    JOptionPane.showMessageDialog(null, "Konnte den Server"
                                + " unter dem gegebenen hostnamen nicht finden","Fehler!", JOptionPane.ERROR_MESSAGE);
			shouldntHappen.printStackTrace(System.err); 
			
		} 
                /*nachdem der host gefunden wurde, wird der benutzername
                * eingegeben.
                */
                Socket serverConnection;
                do {
                logInName = JOptionPane.showInputDialog(null, "Bitte gebe deinen"
                        + "gewünschten Benutzernamen ein",
                        "Einloggen", JOptionPane.PLAIN_MESSAGE);
                //Verbindung mit dem server
                
                
                    serverConnection =  ClientServerCommunicator.logUserIn(logInName);
                    
                } while(serverConnection == null);
                
                
		
		startServerThread(serverConnection); //Thread A
		
		//TODO: Create thread for communication with server (A)
		//TODO: Create thread for broadcasting messages to peers (B) (Make it a Runnable)
		//TODO: Trigger it from a GUI event (InvocationEvent,java.awt.EventQueue!)
		//TODO: Create thread for recieving messages from peers (C)
		//TODO: last but not least, make the GUI
		
		//Thread B is started from the GUI
		
		try {
			startRecieverThread();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		} //Thread C
		
	}

	private static void startRecieverThread() throws SocketException {
		(new ClientMessageReciever()).start();
	}
        
        

        //existingConnection vom ausführen des NEW-Befehls 
        //siehe ClientServerCommunicator.logUserIn()
	private static void startServerThread(Socket existingConnection) {
		(new ClientServerCommunicator(logInName,serverIpAddressTcp.getCanonicalHostName(),existingConnection)).start();
	}

	public static void appendNewMessage(String msgToAppend) {
		chatMessages.append(msgToAppend); 
		/** TODO Eventuell zu aufrufen zu den Aufrufen in die GUI umbauen,
		 *  wenn ich nicht den Stringbuffer in der GUI benutzen kann. 
		 *  Eine alternative scheinen die implementierenden klassen vom 
		 *  Interface AbstractDocumument.Content zu sein,
		 *  also StringContent und GapContent
		 */
	}



}
