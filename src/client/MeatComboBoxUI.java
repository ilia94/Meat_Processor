package client;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class MeatComboBoxUI extends BasicComboBoxUI {
	
	protected JButton createArrowButton() {
	      JButton btn = new JButton() {

			private static final long serialVersionUID = -5888212433918066974L;
	    	  
			protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Image arrowImage = null;
			try {
				arrowImage = ImageIO.read(new File("resources/img/menu/down.png/"));
			} catch (IOException e) {}
			g.drawImage(arrowImage, 0, 0, this.getWidth(), this.getHeight(), this);
			}
	      };
	      btn.setIcon(new ImageIcon("resources/img/menu/red_sliderDown.png"));
	      return btn;
	   }
}
