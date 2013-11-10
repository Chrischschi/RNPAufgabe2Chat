package client_test;

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import client.ClientServerCommunicator;
import server.ChatUser;

public class ClientServerCommunicatorTest {
	
	@Test
	 public void test() {
	  List<ChatUser> users = new ArrayList<ChatUser>();
	  String test = "LIST   4   127.0.0.1      local javalobby.org jLobby";
	  try {
	   users = ClientServerCommunicator.getUsers(test);
	  } catch (UnknownHostException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }
	  assertEquals(2, users.size());
	  System.out.println(users.get(0).chatName + " : " + users.get(0).hostName.getCanonicalHostName());
	  System.out.println(users.get(1).chatName + " : " + users.get(1).hostName.getCanonicalHostName());
	 }

}
