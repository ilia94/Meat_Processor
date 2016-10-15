package network;

public class RegistrationRequest extends NetworkData {

	private static final long serialVersionUID = 8415173526654444303L;
	private String username;
	private String encryptedPassword;
	Boolean result;
	
	public RegistrationRequest(String username, String encryptedPassword) {
		this.username = username;
		this.encryptedPassword = encryptedPassword;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getEncryptedPassword() {
		return encryptedPassword;
	}
	
	public void setResult(Boolean outcome) {
		result = outcome;
	}
	
	public boolean getResult() {
		return result;
	}

}
