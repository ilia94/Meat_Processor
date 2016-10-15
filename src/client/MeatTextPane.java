package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextPane;

public class MeatTextPane extends JTextPane{
	private MeatClientGUI client;
	private OpenFile file;

	private static final long serialVersionUID = 5479061846073263580L;

	public MeatTextPane(MeatClientGUI client, OpenFile file) {
		this.client = client;
		this.file = file;
		setFont(new Font ("KenVector Future", Font.ROMAN_BASELINE, 12));
		setSelectedTextColor(Color.WHITE);
		setSelectionColor(Color.RED);
		addActions();
	}
	
	private void addActions(){
		this.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				if(file.isOnline()){
					client.updateFile(file.getFileID(), getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				
			}
			
		});
	}
}
