package client;

import java.awt.Color;

import javax.swing.JLabel;

import Utilities.Constants;

public class MeatLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	
	{
		setFont(Constants.mySmallFont);
		setForeground(Color.WHITE);
	}
	
	public MeatLabel(String s){
		super(s);
	}

}
