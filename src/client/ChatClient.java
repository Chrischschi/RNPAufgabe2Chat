package client;

import client.gui.ChatClientGUI;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import server.ChatServer;

import server.ChatUser;

public class ChatClient {

    //Konstanten, die in der Spezifikation definiert wurden
    public static final int PEER_PORT = 50001;
    public static final int MAX_CHAT_NAME_LENGTH = 20;
    public static final int MAX_CHAT_MSG_LENGTH = 100;
    public static final Charset REQUIRED_CHARSET = Charset.forName("UTF-8");
    //Abhängige Konstanten 
    public static int DATAGRAM_SIZE = //+1 am ende für das newline
            MAX_CHAT_NAME_LENGTH + ClientSenderThread.COLON_AND_SPACE + MAX_CHAT_MSG_LENGTH + 1;
    /*Liste wird von ClientServerCommunicator befüllt, 
     * mit daten aus einer INFO-nachricht vom server
     */
    public static List<ChatUser> users = Collections.synchronizedList(new ArrayList<ChatUser>());
    static String logInName = ""; //der name, mit dem man sich beim server einloggt
    private static InetAddress serverIpAddressTcp; //addresse des servers
    public static ClientServerCommunicator serverThread = null;
    public static ClientMessageReciever messageReciever = null;

    public static void main(String[] args) {

        /*Der hostname des servers wird in einem dialog vor start der
         eigentlichen GUI eingegeben.
         */
        String serverAddressstring = "";
        boolean serverFound = false;
        do {

            serverAddressstring = JOptionPane.showInputDialog("Bitte hostnamen eingeben");
            try {
                serverIpAddressTcp = InetAddress.getByName(serverAddressstring);
                serverFound = true;
            } catch (UnknownHostException uhx) {
                //in der laborumgebung sollte der host gefunden werden können.
                JOptionPane.showMessageDialog(null, "Konnte den Server"
                        + " unter dem gegebenen hostnamen nicht finden", "Fehler!", JOptionPane.ERROR_MESSAGE);
                serverFound = false;
            }
        } while (!serverFound && serverAddressstring != null);

        /*nachdem der host gefunden wurde, wird der benutzername
         * eingegeben.
         */
        if (serverAddressstring != null) {
            //GUI erstellen.
            final ChatClientGUI gui = new ChatClientGUI();
            String serverResponse = "";
            boolean serverConnectionEstablished = false;
            serverThread = new ClientServerCommunicator(serverIpAddressTcp, gui);

            do {
                logInName = JOptionPane.showInputDialog(null, "Bitte gebe deinen"
                        + " gewünschten Benutzernamen ein",
                        "Einloggen", JOptionPane.PLAIN_MESSAGE);

                //Verbindung mit dem server
                if(logInName!=null) {
                    serverResponse = serverThread.tryLogUserIn(logInName);
                    serverConnectionEstablished = serverResponse.equals(ChatServer.OK_RESPONSE);
                }
                else {//Benutzer hat "Abbrechen" oder X gedrückt
                    System.out.println("Der Benutzer wollte seinen namen nicht"
                            + " eingeben und hat das programm beendet. ");
                    System.exit(0); //verlassen, keine ressourcen wurden verwendet, daher nichts zu schließen.
                } 
                /* Vor erneutem anzeigen des eingabe-dialogs / Starten der 
                 * Chat-GUI wäre es eine nette idee, einen dialog anzuzeigen, 
                 * welcher die empfangene fehler-nachricht vom server / ein 
                 * "Client wurde erfolgreich auf dem Server eingeloggt." 
                 * anzeigt.
                 */
                if(serverConnectionEstablished) {
                    JOptionPane.showMessageDialog(null, "Client wurde erfolgreich auf dem Server eingeloggt.",
                            "Erfolgreich Eingeloggt!", JOptionPane.PLAIN_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, 
                            serverResponse, 
                            "Fehler Beim Einloggen", JOptionPane.ERROR_MESSAGE);
            }

            } while (!serverConnectionEstablished && logInName != null);
            if (logInName != null) {
                System.out.println("name: "+logInName);
                //Starte die abfrage vom server.
                serverThread.start();


                java.awt.EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        //GUI starten
                        gui.setVisible(true);
                    }
                });


                //TODO: Create thread for broadcasting messages to peers (B) (Make it a Runnable)
                //TODO: Trigger it from a GUI event (InvocationEvent,java.awt.EventQueue!)
                //TODO: last but not least, make the GUI

                //Thread B is started from the GUI

                try {
                    messageReciever = createRecieverThread(gui);
                } catch (SocketException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace(System.err);
                } //Thread C
                
                messageReciever.start();
                //Programm ist bereit zum chatten
            }else{
            System.out.println("Keine Name eingegeben, programm wird abgebrochen");
            
        }

        }else{
            System.out.println("Keine Adresse eingegeben, programm wird abgebrochen");
        }


    }

    public static ClientMessageReciever createRecieverThread(ChatClientGUI chatGui) throws SocketException {
       return new ClientMessageReciever(chatGui);
    }

    public static ClientSenderThread createMessageSender(String msgToSend) throws SocketException {
        return new ClientSenderThread(msgToSend, users);
    }
}
