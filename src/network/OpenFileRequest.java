package network;

import java.io.File;

public class OpenFileRequest extends NetworkData {

	private static final long serialVersionUID = 9212166266302422616L;
	@SuppressWarnings("unused")
	private String opener, owner, filename;
	private Integer fileID;
	private File openFile;
	
	public OpenFileRequest(String opener, Integer fileID) {
		this.opener = opener;
		this.fileID = fileID;
	}
	
	public String getOpener() {
		return opener;
	}
	
	public Integer getID() {
		return fileID;
	}
	
	public File getFile() {
		return openFile;
	}
	
	public void setFile(File newFile) {
		openFile = newFile;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getFilename() {
		return filename;
	}
}
