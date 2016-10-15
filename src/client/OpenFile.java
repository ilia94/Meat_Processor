package client;

import java.io.Serializable;

public class OpenFile implements Serializable {
	private static final long serialVersionUID = -7568822364733465460L;
	private String filename;
	private String filepath;
	private boolean online;
	private boolean saved;
	private Integer fileID;
	private String owner;
	
	public OpenFile(String filename, String owner){
		this.owner = owner;
		this.filename = filename;
		this.fileID = null;
		this.filepath = null;
		this.online = false;
		this.saved = false;
	}
	
	public void setOnline(String filename, Integer fileID) {
		this.filename = filename;
		this.fileID = fileID;
		this.online = true;
		this.saved = true;
	}
	
	public void setClosed(){
		this.filename = null;
		this.fileID = null;
		this.online = false;
		this.saved = false;
	}
	
	public void setUnshared() {
		this.fileID = null;
		this.online = false;
		this.saved = false;
	}
	
	public void setOffline(String filename, String filepath) {
		this.filename = filename;
		this.filepath = filepath;
		this.online = false;
		this.saved = true;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public String getFilepath() {
		return filepath;
	}
	
	public boolean isOnline() {
		return online;
	}
	
	public boolean isSaved() {
		return saved;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public Integer getFileID() {
		return fileID;
	}
}
