package network;

public class SaveFileRequest extends NetworkData {

	private static final long serialVersionUID = -7894189108562690839L;
	private String username;
	private String saveText;
	private String filename;
	private boolean result;
	private Integer fileID;
	
	public SaveFileRequest(String user, String name, String Text) {
		username = user;
		saveText = Text;
		filename = name;
	}
	
	public String getText() {
		return saveText;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setResult(boolean r){
		result = r;
	}
	
	public boolean getResult() {
		return result;
	}

	public void setID(Integer fileID) {
		this.fileID = fileID;
	}
	
	public Integer getID(){
		return fileID;
	}
}
