package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

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
	static int COLON_AND_SPACE = 2;
	
	private DatagramSocket outToOtherClients;
	
	private String msgToBroadcast; //kommt von der GUI
	
	private List<ChatUser> deliverTo; //kommt von ChatClient
	
	
	public ClientSenderThread(String msg, List<ChatUser> users) throws SocketException {
		this.outToOtherClients = new DatagramSocket();
		this.msgToBroadcast = msg;
		this.deliverTo = users;
		System.out.println("Created Client Sender Thread on Port " + outToOtherClients.getPort());
	}

	@Override
	public void run() {
		broadCast(msgToBroadcast,deliverTo);
	}

	private void broadCast(String msg, List<ChatUser> recievers) {
		
		for(ChatUser user : recievers) {
			//String, der beim anderen client angezeigt werden soll, zusammenbauen
			String sendString = user.chatName + ": " + msg + "\n"; 
			//String-inhalt als UTF-8 kodiertes byte-array bereitstellen
			byte[] sendData = sendString.getBytes(REQUIRED_CHARSET);
			
			assert sendData.length <= ChatClient.DATAGRAM_SIZE : "sendData won't fit in datagram!"; //TODO remove assert after testing
			System.out.println("Size: " + sendData.length + "\nDatagramm size: " + ChatClient.DATAGRAM_SIZE);
			//Datagramm-paket packen
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length , user.hostName,PEER_PORT);
			System.out.println("Chat Client tries to send message " + msg + " to Peer " + user.chatName +
					" at " + user.hostName );
			sendMsgToPeer(user,sendPacket, msg);
		}
		
	}

	private void sendMsgToPeer(ChatUser u, DatagramPacket sendPacket,String msg)  {
		try {
			outToOtherClients.send(sendPacket);
			System.out.println("Chat Client sent message " + msg + " to Peer " + u.chatName +
					" at " + u.hostName + " !");
		} catch (IOException e) {
			System.err.println("Couldn't deliver Message to " + u.chatName + " at " + u.hostName );
			e.printStackTrace();
		}
		
		
		
	}

}
