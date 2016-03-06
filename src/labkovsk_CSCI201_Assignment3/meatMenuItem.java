package labkovsk_CSCI201_Assignment3;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class meatMenuItem extends JMenuItem {

	private final String itemsPath = "resources/img/menuitems/";
	private final String ext = ".png/";
	private static final long serialVersionUID = -8866485707304882581L;
	private String text;
	private ImageIcon icon;
	
	meatMenuItem(String s) {
		super(s);
		text = s;
		icon = new ImageIcon(itemsPath+s+ext);
		this.setIcon(icon);
	}
	/*
	protected void paintComponent(Graphics g) {
		g.setFont(new Font ("KenVector Future", Font.ROMAN_BASELINE, 12));
		g.drawString(text, 0, 0);
	}
	*/

}
