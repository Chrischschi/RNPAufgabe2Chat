package server_test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.ChatServer;
import server.ChatUser;

public class ChatServerTest {
	
	private ByteArrayOutputStream mySysOut = new ByteArrayOutputStream(); //zum testen von Sysouts
	
	
	
	@Before 
	public void setUpServer() {
		
		ChatServer.loggedInClients = new ArrayList<>();
	}
	@Before 
	public void setUpMyStreams() {
		System.setOut(new PrintStream(mySysOut));
	}

	@Test
	public void testLogClientIn() {
		ChatUser newUser = new ChatUser("Steffen",InetAddress.getLoopbackAddress());
	    ChatServer.logClientIn(newUser); 
	    String userName = "Steffen";
		String hostName = InetAddress.getLoopbackAddress().getCanonicalHostName();
	    //mySysOut ist jetzt voll mit den println-messages aus logClientIn
	    List<String> outputs = Arrays.asList(mySysOut.toString().split("\r\n")); //splitten beim newline => jedes sysout benutzt println!
	    assertTrue("Test Log Client in: Message beim start des einloggens korrekt", outputs.contains(String.format(ChatServer.LOGIN_START_MSG,userName,hostName)));
	    //TODO outputs mit expected messages vergleichen.
	    assertTrue("Test Log Client In: Erfolgreiche Eingeloggt message korrekt",outputs.contains(String.format(ChatServer.SUCCESSFUL_LOGIN_MSG,userName,hostName)));
	    
	    assertTrue("Test Log Client In: Client wurde erfolgreich in die liste eingefügt.",
    			ChatServer.loggedInClients.contains(new ChatUser("Steffen",InetAddress.getLoopbackAddress()))
    		  );
	    
	}

	@Test
	public void testAddClient() {
	
		ChatServer.loggedInClients = new ArrayList<>();
		InetAddress address = null;
		
	    address = InetAddress.getLoopbackAddress();
	
		ChatServer.addClient(new ChatUser("Christian",address));
	    assertTrue("Test Add Client: Einfügen eines neuen Clients in die Liste",
	    			ChatServer.loggedInClients.contains(new ChatUser("Christian",address))
	    		  );
	}
	
	@After
	public void tearDownMyStreams() {
		System.setOut(null);
	}
	
	

}
