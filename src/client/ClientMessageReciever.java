package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;

import static client.ChatClient.*;

public class ClientMessageReciever extends Thread {
	
	private DatagramSocket inFromPeers;
	private boolean serviceRequested = true;
	private String lastRecievedMessage; 
	
	public ClientMessageReciever() throws SocketException {
		inFromPeers = new DatagramSocket(ChatClient.PEER_PORT);
	}
	
	public void run() {
		while(serviceRequested) {
		try {
			lastRecievedMessage = recieveMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateClient(lastRecievedMessage);
		}
	}
	
	/* Sorgt dafür, dass die nachricht in das Chat-Protokoll
	 * eingetragen wird.
	 */
	private void updateClient(String lastRecievedMessage2) {
		// TODO Call to ChatClient
		
	}

	/* Lauscht nach chat-nachrichten und extrahiert den string aus diesen. */
	private String recieveMessage() throws IOException {
		//Paket zum auffangen der nachricht erstellen, das groß 
		DatagramPacket recievedPacket = new DatagramPacket(new byte[DATAGRAM_SIZE], DATAGRAM_SIZE);
		
		System.out.println(this.getClass() + ": Waiting for incoming UDP packets on port " + PEER_PORT );
		//Paket empfangen
		inFromPeers.receive(recievedPacket);
		
		//Inhalt des paketes verarbeiten
		byte[] recievedData = recievedPacket.getData();
		String recievedStringFromData = new String(recievedData,REQUIRED_CHARSET);
		
		//Logging
		System.out.println(this.getClass() + ": Recieved UDP Packet with contents " + recievedStringFromData +
				" from " + recievedPacket.getAddress() + ":" + recievedPacket.getPort()  );
		
		return recievedStringFromData;
	}
	
	

}
