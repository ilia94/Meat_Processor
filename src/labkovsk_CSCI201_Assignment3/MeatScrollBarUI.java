package labkovsk_CSCI201_Assignment3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class MeatScrollBarUI extends BasicScrollBarUI {
	private Image imageThumb, imageTrack;
	private ImageIcon imageDecArrow, imageIncArrow;
	
	public MeatScrollBarUI() {
		try {
            imageThumb = ImageIO.read(new File("resources/img/scrollbar/red_button05.png/"));
            imageTrack = ImageIO.read(new File("resources/img/scrollbar/red_button03.png/"));
            imageDecArrow = new ImageIcon("resources/img/scrollbar/red_sliderUp.png/");
            imageIncArrow = new ImageIcon("resources/img/scrollbar/red_sliderDown.png/");
		} catch (IOException e){}
	}
	
	@Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		 g.translate(trackBounds.x, trackBounds.y);
	    ((Graphics2D)g).drawImage(imageTrack,AffineTransform.getScaleInstance(.35,(double)trackBounds.height/imageTrack.getHeight(null)),null);
	    g.translate( -trackBounds.x, -trackBounds.y );
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
    	g.translate(thumbBounds.x, thumbBounds.y);
        g.drawRect( 0, 0, thumbBounds.width - 2, thumbBounds.height - 1 );
        AffineTransform transform = AffineTransform.getScaleInstance((double)thumbBounds.width/imageThumb.getWidth(null),(double)thumbBounds.height/imageThumb.getHeight(null));
        ((Graphics2D)g).drawImage(imageThumb, transform, null);
        g.translate( -thumbBounds.x, -thumbBounds.y );
    }
    
    @Override
    protected JButton createDecreaseButton(int orientation) {
    	JButton button = new JButton(imageDecArrow);
    	button.setBackground(Color.WHITE);
    	button.setBorder(null);
        return button;
    }
    
    @Override
    protected JButton createIncreaseButton(int orientation) {
    	JButton button = new JButton(imageIncArrow);
    	button.setBackground(Color.WHITE);
    	button.setBorder(null);
        return button;
    }
}
