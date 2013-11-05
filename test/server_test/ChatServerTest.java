package server_test;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.ArrayList;

import org.junit.Test;

import server.ChatServer;
import server.ChatUser;

public class ChatServerTest {

	@Test
	public void testLogClientIn() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddClient() {
		ChatServer c = new ChatServer();
		c.loggedInClients = new ArrayList<>();
		InetAddress address = null;
		
	    address = InetAddress.getLoopbackAddress();
	
		c.addClient(new ChatUser("Christian",address));
	    assertTrue("Test Add Client: Einfügen eines neuen Clients in die Liste",
	    			c.loggedInClients.contains(new ChatUser("Christian",address))
	    		  );
	}

}
