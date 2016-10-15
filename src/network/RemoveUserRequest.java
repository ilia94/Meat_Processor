package network;

public class RemoveUserRequest extends NetworkData {
	private static final long serialVersionUID = -8899547220082610767L;
	private String user;
	private int fileID;
	
	public RemoveUserRequest(int fileID, String username) {
		this.user = username;
		this.fileID = fileID;
	}
	
	public String getUser() {
		return user;
	}
	
	public int getFileID() {
		return fileID;
	}
}
