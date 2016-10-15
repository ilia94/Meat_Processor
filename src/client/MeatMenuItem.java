package client;

import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

public class MeatMenuItem extends JMenuItem {

	private final String itemsPath = "resources/img/menuitems/";
	private final String ext = ".png/";
	private static final long serialVersionUID = -8866485707304882581L;
	private ImageIcon icon;
	
	MeatMenuItem(String s) {
		super(s);
		icon = new ImageIcon(itemsPath+s+ext);
		this.setIcon(icon);
		Font myFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 12); 
		setFont(myFont);
	}

}
