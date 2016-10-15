package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import Utilities.Constants;

public class StartPanelGUI extends JPanel {

	private static final long serialVersionUID = -1088706712355853002L;
	private MeatClientGUI parent;
	private MeatButton loginButton, registerButton, offlineButton;
	private BufferedImage background;
	private String title;
	private boolean connectedServer;
	
	StartPanelGUI(boolean connected, MeatClientGUI meatClientGUI) {
		parent = meatClientGUI;
		connectedServer = connected;
		initializeVariables();
		createGUI();
		addActions();
		setVisible(true);
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(Constants.myHugeFont);
		g.setColor(Color.WHITE);
		g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		int titleWidth = (int) g.getFontMetrics().getStringBounds(title, g).getWidth();
		int startTitleWidth = getWidth()/2 - titleWidth/2;
		 g.drawString(title, startTitleWidth, getHeight()/2);
		repaint();
		revalidate();
	}
	
	void initializeVariables() {
		loginButton = new MeatButton("Log In");
		registerButton = new MeatButton("Register");
		offlineButton = new MeatButton("Offline");
		try {
			background = ImageIO.read(new File("resources/img/backgrounds/meat_panel.png/"));
		} catch (IOException e) {}
		title = "Meat Processor";
		
	}
	
	void createGUI() {
		parent.getContentPane().setBackground(Color.GRAY);
		add(loginButton, BorderLayout.SOUTH);
		add(registerButton, BorderLayout.SOUTH);
		add(offlineButton, BorderLayout.SOUTH);
		if(connectedServer == false) {
			loginButton.setVisible(false);
			registerButton.setVisible(false);
		}
	}
	
	void addActions() {
		offlineButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.launch(false);
			}
		});
		
		loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.changeCard("LOGIN");
			}
		});
		
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.changeCard("REG");
			}
		});
	}
}