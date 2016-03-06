package labkovsk_CSCI201_Assignment3;

import java.awt.Font;

import javax.swing.JLabel;

public class meatLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	
	{
		setFont(new Font ("KenVector Future", Font.ROMAN_BASELINE, 10));
	}
	
	public meatLabel(String s){
		super(s);
	}

}
