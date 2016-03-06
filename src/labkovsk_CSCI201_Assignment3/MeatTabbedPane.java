package labkovsk_CSCI201_Assignment3;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class MeatTabbedPane extends JTabbedPane {
	
	private static final long serialVersionUID = 1L;
	BufferedImage background;
	String title;
	Font font;
	
	public MeatTabbedPane() {
		try {
			background = ImageIO.read(new File("resources/img/backgrounds/meat_panel.png/"));
		} catch (IOException e) {}
		title = "Meat Processor";
		font = new Font ("KenVector Future", Font.ROMAN_BASELINE, 50);  
	}
	
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setFont(font);
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