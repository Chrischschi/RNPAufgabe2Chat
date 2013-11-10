package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

import server.ChatServer;
import server.ChatUser;
import static client.ChatClient.*;


/** Diese klasse sendet die Nachrichten zu den anderen clients,
 *  Ist ein runnable und kein {@link Thread}
 *  damit sie an {@link java.awt.event.InvocationEvent} 
 *  übergeben werden kann!
 * @author Christian
 *
 */
public class ClientSenderThread implements Runnable {
	private static int COLON_AND_SPACE = 2;
	
	private static int DATAGRAM_SIZE =
			ChatClient.MAX_CHAT_NAME_LENGTH + COLON_AND_SPACE  + MAX_CHAT_MSG_LENGTH + 1; 
			//1 für das newline am ende
	
	private DatagramSocket outToOtherClients;
	
	private String msgToBroadcast; //kommt von der GUI
	
	private List<ChatUser> deliverTo; //kommt von ChatClient
	
	
	public ClientSenderThread(String msg, List<ChatUser> users) throws SocketException {
		this.outToOtherClients = new DatagramSocket();
		this.msgToBroadcast = msg;
		this.deliverTo = users;
	}

	@Override
	public void run() {
		broadCast(msgToBroadcast,deliverTo);
	}

	private void broadCast(String msg, List<ChatUser> recievers) {
		
		for(ChatUser u : recievers) {
			String sendString = u.chatName + ": " + msg + "\n"; 
			byte[] sendData = sendString.getBytes(REQUIRED_CHARSET);
			assert(sendData.length <= DATAGRAM_SIZE); //TODO remove assert after testing
			
			DatagramPacket sendPacket = new DatagramPacket(sendData, DATAGRAM_SIZE, u.hostName , PEER_PORT);
			sendMsgTo(u,sendPacket);
		}
		
	}

	private void sendMsgTo(ChatUser u, DatagramPacket sendPacket) {
		// TODO Auto-generated method stub
		
	}

}
