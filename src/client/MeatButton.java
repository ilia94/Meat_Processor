package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.JButton;

import Utilities.Constants;

public class MeatButton extends JButton {

	private Image imageButton, imageButtonSelected;
	private static final long serialVersionUID = 1349441885485894079L;

	MeatButton(String s) {
		super(s);
		try {
			imageButton = ImageIO.read(new File("resources/img/menu/red_button11.png/"));
			imageButtonSelected = ImageIO.read(new File("resources/img/menu/red_button11_selected.png/"));
		} catch (IOException e) {}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setFont(Constants.myFont);
		ButtonModel model = this.getModel();
		if (!model.isRollover()) {
			g.drawImage(imageButton, 0, 0, this.getWidth(), this.getHeight(), this);
		} else {
			g.drawImage(imageButtonSelected, 0, 0, this.getWidth(), this.getHeight(), this);
		}
		if (g.getFontMetrics(Constants.myFont).stringWidth(this.getText()) > this.getWidth()){
			g.setFont(Constants.mySmallFont);
			g.drawString(this.getText(), (this.getWidth()-g.getFontMetrics(Constants.mySmallFont).stringWidth(this.getText()))/2, this.getHeight()/2+g.getFontMetrics(Constants.mySmallFont).getHeight()/4);
		} else {
			g.drawString(this.getText(), (this.getWidth()-g.getFontMetrics(Constants.myFont).stringWidth(this.getText()))/2, this.getHeight()/2+g.getFontMetrics(Constants.myFont).getHeight()/4);
		}
		setForeground(Color.WHITE);
	}
}
