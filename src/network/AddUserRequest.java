package network;

public class AddUserRequest extends NetworkData {

	private static final long serialVersionUID = -8899547220082610767L;
	@SuppressWarnings("unused")
	private String fromUser, toUser, filename;
	private int fileID;
	
	public AddUserRequest(int fileID,  String toUser, String fromUser) {
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.fileID = fileID;
	}
	
	public String getFromUser() {
		return fromUser;
	}
	
	public String getToUser() {
		return toUser;
	}
	
	public int getFileID() {
		return fileID;
	}
}
