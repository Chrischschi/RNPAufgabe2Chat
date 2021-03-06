package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import static client.ChatClient.*;
import client.gui.ChatClientGUI;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import server.ChatUser;


public class ClientMessageReciever extends Thread {
    /**
     * Wird an den Client gesendet, um ihn zu löschen
     */
    private static final String INTERNAL_MESSAGE_UNBLOCK = "unblock";
	
	private DatagramSocket inFromPeers;
	private String lastRecievedMessage; 
        private ChatClientGUI chatGuiReference;
        
	
	public ClientMessageReciever(ChatClientGUI chatGui) throws SocketException {
                this.chatGuiReference = chatGui;
		inFromPeers = new DatagramSocket(ChatClient.PEER_PORT);
	}
	
    @Override
	public void run() {
		while(!isInterrupted()) {
		try {
			lastRecievedMessage = recieveMessage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(System.err);
		}
		if(!lastRecievedMessage.equals(INTERNAL_MESSAGE_UNBLOCK)) updateClient(lastRecievedMessage);
		} 
                
                inFromPeers.close();
                
                System.out.println("ClientMessageReciever Stopp");
	}
	
	/* Sorgt dafür, dass die nachricht in das Chat-Protokoll
	 * eingetragen wird.
	 */
	private void updateClient(final String msgToAppend) {
               java.awt.EventQueue.invokeLater(new Runnable(){
                   @Override
                   public void run() {
                       chatGuiReference.chatProtocol.append(msgToAppend);
                   } 
               });
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
		String recievedStringFromData = new String(recievedData,REQUIRED_CHARSET).trim() + "\n";
		//Logging
		System.out.println(this.getClass() + ": Recieved UDP Packet with contents " + recievedStringFromData +
				" from " + recievedPacket.getAddress() + ":" + recievedPacket.getPort()  );
		
		return recievedStringFromData;
	}

        
    /**
     * Schließt den Datagramm-Socket des Threads von außerhalb des Threads, 
     * aber noch innerhalb des clients.
     */
    public void sendUnblockMessageToReciever() {
            try {
                List<ChatUser> localHostList = Arrays.asList(new ChatUser("localhost", InetAddress.getLoopbackAddress()));
                new Thread(new ClientSenderThread(INTERNAL_MESSAGE_UNBLOCK,localHostList)).start();
            } catch (SocketException ex) { //Wenn hier eine exception auftritt
                ex.printStackTrace(); 
            }
        
        System.out.println("Closed Socket inFromPeers!");
    }

}
