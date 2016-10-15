package network;

public class PullRequest extends NetworkData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 718408444065081987L;
	private Integer fileID;
	private String username;
	private String text;
	private boolean isOnline;
	
	public PullRequest(Integer fileID, String username){
		this.fileID = fileID;
		this.username = username;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public Integer getFileID() {
		return fileID;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}
	
	public boolean isOnline() {
		return isOnline;
	}
	
}
