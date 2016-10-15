package server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import Utilities.Constants;

public class MeatServerGUI extends JFrame {
	private static final long serialVersionUID = 1236723831659451333L;
	private static JTextArea textArea;
	private JScrollPane textAreaScrollPane;
	private JButton StartStopButton;
	private ServerSocket ss;
	private ServerListener serverListener;
	private MeatServer server;
	private int port;
	
	public MeatServerGUI(MeatServer meatServer, int p) {
		super(Constants.serverGUITitle);
		port = p;
		server = meatServer;
		ss = null;
		initializeVariables();
		createGUI();
		addActionAdapters();
		setVisible(true);
	}
	
	private void initializeVariables() {
		textArea = new JTextArea();
		textArea.setEditable(false);
		textAreaScrollPane = new JScrollPane(textArea);
		StartStopButton = new JButton("Start");
	}
	
	private void createGUI() {
		setSize(Constants.serverGUIWidth, Constants.serverGUIHeight);
		JPanel southPanel = new JPanel();
		southPanel.add(StartStopButton);
		add(southPanel, BorderLayout.SOUTH);
		add(textAreaScrollPane, BorderLayout.CENTER);
	}
	
	private void addActionAdapters() {
		StartStopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (StartStopButton.getText().equals("Start")) {
					try {
						ss = new ServerSocket(port);
						System.out.println("Server Socket opened on port: " + port);
					} catch (IOException e1) {
						System.out.println("Invalid Server Socket");
					}
					server.runServer();
					serverListener = new ServerListener(server, ss);
					serverListener.start();
					StartStopButton.setText("Stop");
				}
				else {
					server.stopServer();
					try {
						ss.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					StartStopButton.setText("Start");
				}
			}
			
		});
		
		addWindowListener(new WindowAdapter() {
				  public void windowClosing(WindowEvent we) {
					  System.exit(0);
				  }
			});
	}
	
	public static void addMessage(String msg) {
		if (textArea.getText() != null && textArea.getText().trim().length() > 0) {
			textArea.append("\n" + msg);
		}
		else {
			textArea.setText(msg);
		}
	}
}
