package network;

public class UserLookup extends NetworkData {

	private static final long serialVersionUID = 1098041949079923652L;
	private String username;
	private boolean response;
	
	public UserLookup(String name) {
		username = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setResponse(boolean b) {
		response = b;
	}
	
	public boolean getResponse() {
		return response;
	}

}
