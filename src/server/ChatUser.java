package server;

import java.net.InetAddress;

public class ChatUser {
	/**
	 * jeweils Chat-Name des Benutzers und Hostname des Clients
	 */
	public final String chatName; 
	
	public final InetAddress hostName;
	
	public ChatUser(String chatName, InetAddress hostName) {
		this.chatName = chatName;
		this.hostName = hostName;
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof ChatUser)) return false;
		ChatUser that = (ChatUser) o;
		return this.chatName.equals(that.chatName) && 
			   this.hostName.equals(that.hostName);
	}
        /** Tostring implementiert, damit es in der JList verständlicher
         * aussieht.
         * 
         * @return ein string
         */
        @Override
        public String toString() {
            return chatName + "@" + hostName.getCanonicalHostName();
        }
	

}
