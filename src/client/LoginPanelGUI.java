package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Utilities.Constants;

public class LoginPanelGUI extends JPanel {
	private static final long serialVersionUID = -8583807690938863884L;
	private MeatClientGUI client;
	private MeatLabel usernameLabel, passwordLabel;
	private MeatButton loginButton, backButton;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private BoxLayout boxLayout;
	
	LoginPanelGUI(MeatClientGUI meatClientGUI) {
		client = meatClientGUI;
		initializeVariables();
		createGUI();
		addActions();
	}
	
	void initializeVariables() {
		usernameLabel = new MeatLabel("Username");
		passwordLabel = new MeatLabel("Password");
		loginButton = new MeatButton("Log In");
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		backButton = new MeatButton("Back");
		boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		
	}
	
	void createGUI() {
		setLayout(boxLayout);
		add(Box.createRigidArea(new Dimension(200,200)));
		usernameField.setFont(Constants.myFont);
		usernameField.setMaximumSize(new Dimension(250,30));
		passwordField.setMaximumSize(new Dimension(250,30));
		add(usernameLabel);
		add(usernameField);
		add(passwordLabel);
		add(passwordField);
		add(loginButton);
		add(Box.createVerticalStrut(40));
		add(backButton);
		
	}
	
	void addActions() {
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.changeCard("START");
			}
		});
		
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int error = client.login(usernameField.getText(), new String(passwordField.getPassword()));
				if (error == 2) {
					JOptionPane.showMessageDialog(client,
						    "Login Failed. Cannot Connect to Server.",
						    "Server Error",
						    JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(Constants.myHugeFont);
		g.setColor(Color.WHITE);
		g.drawImage(client.background, 0, 0, getWidth(), getHeight(), null);
		repaint();
		revalidate();
	}
}
