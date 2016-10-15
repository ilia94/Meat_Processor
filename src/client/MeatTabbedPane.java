package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import Utilities.Constants;

public class MeatTabbedPane extends JTabbedPane {
	
	private static final long serialVersionUID = 1L;
	BufferedImage background;
	String title;
	
	public MeatTabbedPane() {
		try {
			background = ImageIO.read(new File("resources/img/backgrounds/meat_panel.png/"));
		} catch (IOException e) {}
		title = "Meat Processor";
	}
	
	 public Color getForegroundAt(int index){
	        return Color.WHITE;
	        }
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(Constants.myHugeFont);
		g.setColor(Color.WHITE);
		JPanel currentTab = (JPanel)getSelectedComponent();
		if (currentTab == null) {
			int titleWidth = (int) g.getFontMetrics().getStringBounds(title, g).getWidth();
			int startTitleWidth = getWidth()/2 - titleWidth/2;
			g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
		    g.drawString(title, startTitleWidth, getHeight()/2);
		}
	}
}