package client;

import client.gui.ChatClientGUI;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
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
	public static List<ChatUser> users = Collections.synchronizedList(new ArrayList<ChatUser>());
	
	
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
        
		boolean serverFound = false;
		do {

			String serverAddressstring = JOptionPane.showInputDialog("Bitte hostnamen eingeben");
			try {
				serverIpAddressTcp = InetAddress.getByName(serverAddressstring);
				serverFound = true;
			} catch (UnknownHostException uhx) {
				//in der laborumgebung sollte der host gefunden werden können.
				JOptionPane.showMessageDialog(null, "Konnte den Server"
						+ " unter dem gegebenen hostnamen nicht finden","Fehler!", JOptionPane.ERROR_MESSAGE);
				serverFound = false;
			} 
		} while(!serverFound);

                /*nachdem der host gefunden wurde, wird der benutzername
                * eingegeben.
                */
                boolean serverConnectionEstablished = false;
                ClientServerCommunicator serverThread = new ClientServerCommunicator(serverIpAddressTcp);
                
                do {
                logInName = JOptionPane.showInputDialog(null, "Bitte gebe deinen"
                        + "gewünschten Benutzernamen ein",
                        "Einloggen", JOptionPane.PLAIN_MESSAGE);
                
                //Verbindung mit dem server
                serverConnectionEstablished = serverThread.tryLogUserIn(logInName);
                
                		
                } while(!serverConnectionEstablished);
                
                
		//Starte die abfrage vom server.
		serverThread.start();
		
		//GUI starten.
		final ChatClientGUI gui = new ChatClientGUI();
		
	
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui.setVisible(true);
            }
        });
	
		
		//TODO: Create thread for broadcasting messages to peers (B) (Make it a Runnable)
		//TODO: Trigger it from a GUI event (InvocationEvent,java.awt.EventQueue!)
		//TODO: last but not least, make the GUI
		
		//Thread B is started from the GUI
		
		try {
			startRecieverThread(gui);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		} //Thread C
		
	}

	private static void startRecieverThread(ChatClientGUI chatGui) throws SocketException {
		(new ClientMessageReciever(chatGui)).start();
	}

    public static ClientSenderThread sendMessages(String msgToSend) {
        return new ClientSenderThread(msgToSend,users);
    }
        
        




}
