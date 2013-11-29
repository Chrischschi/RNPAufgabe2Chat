package client.gui;

import client.ChatClient;
import client.ClientSenderThread;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.SocketException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import server.ChatUser;

public class ChatFrame extends JFrame {

    private final static float scale = .78125f; //800*600
    //private final static float scale = 1.0f; //1024*768
    public final static int width = 1024;
    public final static int height = 768;
    public JPanel borderPanel = new JPanel() {
        public void paintComponent(Graphics g) {
            g.drawImage(Util.getBorderImage(scale(width), scale(height)).getImage(), 0, 0, null);
        }
    };
    public JPanel panel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(Util.getOverlayImage(scale(width - 60), scale(height - 60)).getImage(), 0, 0, null);
        }
    };
    public JButton sendButton = new JButton();
    public JButton exitButton = new JButton();
    ActionListener exitListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.exit(0);

        }
    };
    ActionListener sendListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            String line = eingabe.getText();
            eingabe.setText("");
            try {
                ClientSenderThread c = ChatClient.createMessageSender(line);
                java.awt.EventQueue.invokeLater(c);
            } catch (SocketException e) {
                JOptionPane.showMessageDialog(rootPane, "Es konnte kein socket für das verschicken der nachricht erzeugt werden!", "ClientSenderThread fehler", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    };
    public JTextArea chat = new JTextArea("", 50, 50);
    JPanel chatPanel = new JPanel() {
        {
            setOpaque(false);
        }

        public void paint(Graphics g) {
            g.drawImage(Util.getChatImage(scale(673), scale(568)).getImage(), 0, 0, this);
            super.paint(g);
        }
    };
    JScrollPane scrollPane = new JScrollPane(chat);
    public static DefaultListModel<String> userList = new DefaultListModel<String>();
    public JList<String> users = new JList<String>(userList) {
        {
            setOpaque(false);
        }

        public void paint(Graphics g) {
            g.drawImage(Util.getUserListImage(scale(200), scale(568)).getImage(), 0, 0, this);
            super.paint(g);
        }
    };
    public JTextField eingabe = new JTextField() {
        {
            setOpaque(false);
        }

        public void paint(Graphics g) {
            g.drawImage(Util.getEingabeImage(scale(673), scale(50)).getImage(), 0, 0, this);
            super.paint(g);
        }
    };
    String localUser = "";

    public ChatFrame(ChatUser user) {
        this(user.chatName);
    }

    public ChatFrame(String user) {
        localUser = user;
        this.setResizable(false);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initPanels();
        this.add(borderPanel);
        this.pack();
    }

    public static void main(String[] args) {
        new ChatFrame("Test").setVisible(true);
    }

    private void initPanels() {
        borderPanel.setPreferredSize(new Dimension(scale(width), scale(height)));
        borderPanel.setLayout(null);
        panel.setBounds(scale(30), scale(30), scale(width - 60), scale(height - 60));
        panel.setLayout(null);
        initButtons();
        initTextAreas();
        borderPanel.add(panel);
    }

    private void initTextAreas() {
        scrollPane.setBounds(scale(30), scale(30), scale(673), scale(568));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        chatPanel.setBounds(scale(30), scale(30), scale(673), scale(568));
        chatPanel.add(scrollPane);
        chat.setPreferredSize(new Dimension(scale(653), scale(568)));
        chat.setLineWrap(true);
        chat.setWrapStyleWord(true);
        chat.setEditable(false);
        //addLine("test",Util.BLIND_TEXT);
        eingabe.setBounds(scale(30), scale(628), scale(673), scale(50));
        eingabe.setBorder(BorderFactory.createEmptyBorder());
        users.setBounds(scale(733), scale(30), scale(200), scale(568));
        panel.add(users);
        panel.add(eingabe);
        panel.add(scrollPane);
    }

    private void initButtons() {
        exitButton.setBounds(scale(950), scale(-2), scale(30), scale(31));
        setButtonIcons(exitButton, Util.getNormalExitIcon(scale(30), scale(30)), Util.getPressedExitIcon(scale(30), scale(30)));
        exitButton.addActionListener(exitListener);
        sendButton.setBounds(scale(733), scale(628), scale(200), scale(50));
        setButtonIcons(sendButton, Util.getNormalButtonIcon(scale(200), scale(50)), Util.getPressedButtonIcon(scale(200), scale(50)));
        sendButton.setText("Send");
        sendButton.addActionListener(sendListener);
        panel.add(sendButton);
        borderPanel.add(exitButton);
    }

    private void setButtonIcons(JButton button, Icon normalIcon, Icon pressedIcon) {
        button.setIcon(normalIcon);
        button.setPressedIcon(pressedIcon);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setForeground(new Color(0xDDDDDD));
        button.setHorizontalTextPosition(0);
    }

    public void addLine(String user, String line) {
        chat.setText(chat.getText() + user + ": " + line + "\n");
    }

    public void addUser(String user) {
        userList.addElement(user);
    }

    public void addUser(ChatUser user) {
        addUser(user.chatName);
    }

    public void removeUser(String user) {
        userList.removeElement(user);
    }

    public void removeUser(ChatUser user) {
        removeUser(user.chatName);
    }

    private int scale(int value) {
        return new Float(value * scale).intValue();
    }

    public void sendLine(String line) {
        //TODO
    }
}
