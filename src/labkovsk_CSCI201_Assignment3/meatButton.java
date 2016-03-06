package labkovsk_CSCI201_Assignment3;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonModel;
import javax.swing.JButton;

public class meatButton extends JButton {

	private Image imageButton, imageButtonSelected;
	private static final long serialVersionUID = 1349441885485894079L;

	meatButton(String s) {
		super(s);
		try {
			imageButton = ImageIO.read(new File("resources/img/menu/red_button11.png/"));
			imageButtonSelected = ImageIO.read(new File("resources/img/menu/red_button11_selected.png/"));
		} catch (IOException e) {}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Font myFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 14);
		Font smallFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 10);
		g.setFont(myFont);
		ButtonModel model = this.getModel();
		if (!model.isRollover()) {
			g.drawImage(imageButton, 0, 0, this.getWidth(), this.getHeight(), this);
		} else {
			g.drawImage(imageButtonSelected, 0, 0, this.getWidth(), this.getHeight(), this);
		}
		if (g.getFontMetrics(myFont).stringWidth(this.getText()) > this.getWidth()){
			g.setFont(smallFont);
			g.drawString(this.getText(), (this.getWidth()-g.getFontMetrics(smallFont).stringWidth(this.getText()))/2, this.getHeight()/2+g.getFontMetrics(smallFont).getHeight()/4);
		} else {
			g.drawString(this.getText(), (this.getWidth()-g.getFontMetrics(myFont).stringWidth(this.getText()))/2, this.getHeight()/2+g.getFontMetrics(myFont).getHeight()/4);
		}
	}
}
