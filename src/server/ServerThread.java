package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Vector;
import client.OpenFile;
import network.LoginRequest;
import network.OpenFileRequest;
import network.PullRequest;
import network.AddUserRequest;
import network.FileInfoRequest;
import network.AccesibleFileRequest;
import network.RegistrationRequest;
import network.RemoveUserRequest;
import network.SaveFileRequest;
import network.UpdateFileRequest;
import network.UserLookup;

public class ServerThread extends Thread {
	protected Socket socket;
	protected ObjectInputStream ois;
	protected ObjectOutputStream oos;
	MeatServer server;
	
	public ServerThread(MeatServer meatServer, Socket s) throws IOException {
		server = meatServer;
		socket = s;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}
	
	public void closeSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		try {
			while(true) {
				Object message = ois.readObject();
				if (message instanceof LoginRequest) {
					Boolean result = server.checkLogin(((LoginRequest) message).getUsername(), ((LoginRequest) message).getEncryptedPassword());
					((LoginRequest) message).setResult(result);
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof RegistrationRequest) {
					String username = ((RegistrationRequest) message).getUsername();
					String encryptedPassword = ((RegistrationRequest) message).getEncryptedPassword();
					Boolean result = server.register(username, encryptedPassword);
					((RegistrationRequest) message).setResult(result);
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof AccesibleFileRequest) {
					 Vector<Integer> FileIDs = server.getAccesibleFiles(((AccesibleFileRequest) message).getUsername());
					 HashMap<String,Vector<OpenFile>> ownerFiles = server.getFileInformation(FileIDs);
					 ((AccesibleFileRequest) message).setFileInfo(ownerFiles);
					 oos.writeObject(message);
					 oos.flush();
				}
				else if (message instanceof OpenFileRequest){
					((OpenFileRequest) message).setFile(server.openFile(((OpenFileRequest) message).getID()));
					((OpenFileRequest) message).setFilename(server.getFilename(((OpenFileRequest) message).getID()));
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof SaveFileRequest){
					Integer fileID = server.saveFile(((SaveFileRequest) message).getUsername(),((SaveFileRequest) message).getFilename(), ((SaveFileRequest) message).getText());
					((SaveFileRequest) message).setID(fileID);
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof UserLookup){
					((UserLookup) message).setResponse(server.userExists(((UserLookup) message).getUsername()));
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof AddUserRequest) {
					server.addUser(((AddUserRequest) message).getFileID(), ((AddUserRequest) message).getToUser());
				}
				else if (message instanceof RemoveUserRequest) {
					server.removeUser(((RemoveUserRequest) message).getFileID(), ((RemoveUserRequest) message).getUser());
				}
				else if (message instanceof FileInfoRequest) {
					Integer fileID = ((FileInfoRequest) message).getID();
					((FileInfoRequest) message).setFilename(server.getFilename(fileID));
					((FileInfoRequest) message).setOwner(server.getOwner(fileID));
					((FileInfoRequest) message).setSharedUsers(server.getSharedUsersOnFile(fileID));
					oos.writeObject(message);
					oos.flush();
				}
				else if (message instanceof UpdateFileRequest) {
					Integer fileID = ((UpdateFileRequest) message).getFileID();
					String text = ((UpdateFileRequest) message).getText();
					server.updateFileText(fileID, text);
				}
				
				else if (message instanceof PullRequest) {
					Integer fileID = ((PullRequest) message).getFileID();
					String username = ((PullRequest) message).getUsername();
					boolean isOnline = server.isShared(fileID, username);
					String text = server.getLatestText(fileID);
					((PullRequest) message).setText(text);
					((PullRequest) message).setOnline(isOnline);
					oos.writeObject(message);
					oos.flush();
				}
			}
		} catch (ClassNotFoundException cnfe) {
			System.out.println("cnfe in run: " + cnfe.getMessage());
		} catch (IOException ioe) {
		}
	}
}
