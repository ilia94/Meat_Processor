package network;

import java.util.Vector;

public class FileInfoRequest extends NetworkData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7066910703610073477L;
	private Integer fileID;
	private String filename;
	private String owner;
	private Vector<String> sharedUsers;
	
	public FileInfoRequest(Integer fileID) {
		this.fileID = fileID;
	}
	
	public Integer getID() {
		return fileID;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public void setSharedUsers(Vector<String> sharedUsers) {
		this.sharedUsers = sharedUsers;
	}
	
	public Vector<String> getSharedUsers() {
		return sharedUsers;
	}
}
