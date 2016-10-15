package network;

import java.util.HashMap;
import java.util.Vector;

import client.OpenFile;

public class AccesibleFileRequest extends NetworkData {

	private static final long serialVersionUID = -1871074311940506821L;
	private String username;
	private HashMap<String,Vector<OpenFile>> ownerFiles;
	
	public AccesibleFileRequest(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setFileInfo(HashMap<String,Vector<OpenFile>> ownerFiles) {
		this.ownerFiles = ownerFiles;
	}
	
	public HashMap<String,Vector<OpenFile>> getFileInfo() {
		return ownerFiles;
	}
}
