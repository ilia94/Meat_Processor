package network;

public class UpdateFileRequest extends NetworkData{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8471359904237506882L;
	private Integer fileID;
	private String text;
	
	public UpdateFileRequest(Integer fileID, String text){
		this.fileID = fileID;
		this.text = text;
	}
	
	public Integer getFileID() {
		return fileID;
	}
	
	public String getText() {
		return text;
	}
}
