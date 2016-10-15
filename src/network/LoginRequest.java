package network;

public class LoginRequest extends NetworkData {

	private static final long serialVersionUID = 7186140854193693580L;
	private String username;
	private String encryptedPassword;
	Boolean result;
	
	public LoginRequest(String username, String encryptedPassword) {
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
