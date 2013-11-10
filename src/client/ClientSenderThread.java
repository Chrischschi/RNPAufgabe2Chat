package client;

import java.net.DatagramSocket;
import java.net.SocketException;


/** Diese klasse sendet die Nachrichten zu den anderen clients,
 *  Ist ein runnable und kein {@link Thread}
 *  damit sie an {@link java.awt.event.InvocationEvent} 
 *  übergeben werden kann!
 * @author Christian
 *
 */
public class ClientSenderThread implements Runnable {
	
	private DatagramSocket outToOtherClients;
	
	private String msgToBroadcast; //kommt von der GUI
	
	public ClientSenderThread(String msg) throws SocketException {
		outToOtherClients = new DatagramSocket();
		msgToBroadcast = msg;
	}

	@Override
	public void run() {
		
	
		
	}

}
