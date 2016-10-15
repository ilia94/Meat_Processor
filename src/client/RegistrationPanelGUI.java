package client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import Utilities.Constants;

public class RegistrationPanelGUI extends JPanel {

	private static final long serialVersionUID = 1822345327262161265L;
	private MeatClientGUI client;
	private JTextField usernameField;
	private JPasswordField passwordField, confrimPassField;
	private MeatButton registerButton, backButton;
	private MeatLabel usernameLabel, passwordLabel;
	private String username, password;
	private BoxLayout boxLayout;
	
	RegistrationPanelGUI(MeatClientGUI meatClientGUI) {
		client = meatClientGUI;
		initializeVariables();
		createGUI();
		addActions();
		
	}
	
	void initializeVariables() {
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		confrimPassField = new JPasswordField();
		registerButton = new MeatButton("Sign Up");
		passwordLabel = new MeatLabel("Password");
		boxLayout = new BoxLayout(this, BoxLayout.PAGE_AXIS);
		usernameLabel = new MeatLabel("Username");
		passwordLabel = new MeatLabel("Password");
		backButton = new MeatButton("Back");
	}
	
	void createGUI() {
		setLayout(boxLayout);
		add(Box.createRigidArea(new Dimension(200,200)));
		usernameField.setFont(Constants.myFont);
		usernameField.setMaximumSize(new Dimension(250,30));
		passwordField.setMaximumSize(new Dimension(250,30));
		confrimPassField.setMaximumSize(new Dimension(250,30));
		add(usernameLabel);
		add(usernameField);
		add(passwordLabel);
		add(passwordField);
		add(confrimPassField);
		add(registerButton);
		add(Box.createVerticalStrut(40));
		add(backButton);
	}
	
	void addActions() {
		backButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.changeCard("START");
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				char[] pass = passwordField.getPassword();
				char[] confirm = confrimPassField.getPassword();
				username = usernameField.getText();
				password = new String(passwordField.getPassword());
				if(!passFormat(pass))
					JOptionPane.showMessageDialog(client,
						    "Make sure your password contains an uppercase letter and a number",
						    "Password Error",
						    JOptionPane.WARNING_MESSAGE);
				else if(!passMatch(pass, confirm))
					JOptionPane.showMessageDialog(client,
						    "Passwords do not match",
						    "Password Error",
						    JOptionPane.WARNING_MESSAGE);
				else{
					int error = client.register(username, password);
					if (error == 2) {
						JOptionPane.showMessageDialog(client,
							    "Sign Up Failed. Cannot Connect to Server.",
							    "Server Error",
							    JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		});
	}
	
	boolean passFormat(char[] entered) {	
		boolean upper = false;
		boolean number = false;
		for (int i = 0; i < entered.length; i++) {
			int ascii = (int)(entered[i]);
			if (ascii > 47 && ascii < 58) {
				number = true;
			}
			if (ascii > 64 && ascii < 91) {
				upper = true;
			}
		}
		if (upper && number)
			return true;
		else
			return false;
	}
	
	boolean passMatch(char[] a, char[] b) {
		if (Arrays.equals(a,b))
			return true;
		else
			return false;
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
