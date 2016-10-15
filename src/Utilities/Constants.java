package Utilities;

import java.awt.Font;

public class Constants {
	//Client GUI
	public static final Font myFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 13);
	public static final Font mySmallFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 10);
	public static final Font myHugeFont = new Font ("KenVector Future", Font.ROMAN_BASELINE, 50);
	public static final String clientGUITitleString = new String("Meat Processor");
	public static final Integer clientGUIWidth = 700;
	public static final Integer clientGUIHeight = 500;

	//Server
	public static final String resourcePath = new String("resources/");
	public static final String serverConfigFilePath = new String("server-config.properties");
	public static final String clientConfigFilePath = new String("client-config.properties");
	public static final String serverDrivePath = new String("drive/");
	
	//ServerGUI
	public static final String serverGUITitle = new String("Meat Server");
	public static final String initialServerMessage = new String("Server Started on Port: ");
	public static final String endServerMessage = new String("Server Stopped");
	public static final Integer serverGUIWidth = 500;
	public static final Integer serverGUIHeight = 500;
	public static final Integer openUser = 1;
	public static final Integer removeUser = 2;
	
	//Client Connection Manager

}
