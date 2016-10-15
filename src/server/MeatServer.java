package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Utilities.Constants;
import client.OpenFile;
import server.diff_match_patch.Patch;


public class MeatServer {
	private Connection databaseConnection;
	private volatile boolean running;
	private HashMap<Integer, Queue<String>> fileUpdateQueue;
	
	public MeatServer(int port) {
		fileUpdateQueue = new HashMap<Integer,Queue<String>>();
		running = false;
		connectDatabase();
		new MeatServerGUI(this, port);
	}
	
	public void runServer() {
		running = true;
	}
	
	public void stopServer() {
		running = false;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	private void connectDatabase() {
		databaseConnection = null;
		try {
		Class.forName("com.mysql.jdbc.Driver");
		databaseConnection = DriverManager.getConnection("jdbc:mysql://localhost/MeatDatabase?user=root&password=");
		} catch(SQLException sqle) {
			System.out.println("sqle: " + sqle.getMessage());
		} catch(ClassNotFoundException cnfe) {
			System.out.println("cnfe: " + cnfe.getMessage());
		} 
	}
	
	public Boolean checkLogin(String username, String encryptedPassword) {
		Boolean result = null;
		try {
			MeatServerGUI.addMessage("Log In Attempt" + " User: " + username + " Password: " + encryptedPassword);
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				String storedPassword = rs.getString("password");
				if (storedPassword.equals(encryptedPassword)) {
					MeatServerGUI.addMessage("Log In Succeeded " + " User: " + username);
					result = true;
				} else {
					MeatServerGUI.addMessage("Log In Failed " + " User: " + username);
					result = false;
					}
			}
			else {
				result = false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Boolean register(String username, String encryptedPassword) {
		Boolean result = null;
		try {
			MeatServerGUI.addMessage("Sign Up Attempt" + " User: " + username + " Password: " + encryptedPassword);
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				result = true;
				MeatServerGUI.addMessage("Sign Up Succeeded " + " User: " + username);
				ps = databaseConnection.prepareStatement("INSERT INTO Users (username, password) VALUES (?, ?)");
				ps.setString(1, username);
				ps.setString(2, encryptedPassword);
				ps.executeUpdate();
				File cloudDrive = new File(Constants.serverDrivePath + username);
				if (!cloudDrive.exists()) {
				    try{
				    	cloudDrive.mkdir();
				    } catch(SecurityException e) {
				    	System.out.println("Security Exception in MeatServer");
				    }        
				} else {
					System.out.println("Directory already exists");
				}
			} else {
				result = false;
				MeatServerGUI.addMessage("Sign Up Failed" + " User: " + username);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Vector<String> ownFileNames(String username) {
		Vector<String> files = new Vector<String>();
		File cloudDrive = new File(Constants.serverDrivePath + username);
		String[] fileList = cloudDrive.list();
		for(String filename : fileList) {
			files.add(filename);
		}
		return files;
	}
	
	public Vector<Integer> getAccesibleFiles(String username) {
		Vector<Integer> fileIDs = new Vector<Integer>();
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM FileShare WHERE sharedUser=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				Integer ID = rs.getInt("fileID");
				fileIDs.add(ID);
			}
			ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE owner=?");
			ps.setString(1, username);
			rs = ps.executeQuery();
			while(rs.next()) {
				Integer ID = rs.getInt("fileID");
				fileIDs.add(ID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fileIDs;
	}
	
	public HashMap<String,Vector<OpenFile>> getFileInformation(Vector<Integer> fileIDs) {
		try {
			HashMap<String,Vector<OpenFile>> ownerFiles = new HashMap<String,Vector<OpenFile>>();
			for(Integer ID : fileIDs) {
				PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE fileID=?");
				ps.setInt(1, ID);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					String ownerName = rs.getString("owner");
					if(!ownerFiles.containsKey(ownerName)){
						Vector<OpenFile> newVector = new Vector<OpenFile>();
						ownerFiles.put(ownerName, newVector);
					}
					OpenFile newFile = new OpenFile(rs.getString("filename"), ownerName);
					newFile.setOnline(rs.getString("filename"), ID);
					ownerFiles.get(ownerName).add(newFile);
				}
			}
			return ownerFiles;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public File openFile(Integer ID) {
		String owner = null;
		String filename = null;
		PreparedStatement ps;
		File cloudFile = null;
		try {
			ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE fileID=?");
			ps.setInt(1, ID);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				owner = rs.getString("owner");
				filename = rs.getString("filename");
			}
			cloudFile = new File(Constants.serverDrivePath + owner + "/" + ID);
			if(cloudFile.exists()){
				MeatServerGUI.addMessage("File Downloaded " + " Owner: " + owner + " File: " + filename);
				return cloudFile;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Integer saveFile(String username, String filename, String saveText) {
		Integer fileID = null;
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("INSERT INTO Files (filename, owner) VALUES (?, ?)");
			ps.setString(1, filename);
			ps.setString(2, username);
			ps.executeUpdate();
			
			ps = databaseConnection.prepareStatement("SELECT LAST_INSERT_ID() AS LastID FROM Files");
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				fileID = rs.getInt("LastID");
			}
			
			File saveFile = new File(Constants.serverDrivePath + username + "/" + fileID);
			FileWriter fw = new FileWriter(saveFile);
			fw.write(saveText);
			fw.close();
			
			MeatServerGUI.addMessage("File Uploaded" + " User: " + username + " Filename: " + filename + " File ID: " + fileID);
			
			fileUpdateQueue.put(fileID, new LinkedList<String>());
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fileID;
	}
	
	public boolean userExists(String username) {
		boolean found = false;
		MeatServerGUI.addMessage("User Check." + " User: " + username);
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Users WHERE username=?");
			ps.setString(1, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				found = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return found;
	}
	
	public String getFilename(Integer fileID) {
		String filename = null;
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE fileID=?");
			ps.setInt(1, fileID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				filename = rs.getString("filename");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return filename;
	}
	
	public String getOwner(Integer fileID) {
		String owner = null;
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE fileID=?");
			ps.setInt(1, fileID);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				owner = rs.getString("owner");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return owner;
	}
	
	public Vector<String> getSharedUsersOnFile(Integer fileID){
		Vector<String> users = null;
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM FileShare WHERE fileID=?");
			ps.setInt(1, fileID);
			ResultSet rs = ps.executeQuery();
			users = new Vector<String>();
			while(rs.next()) {
				String sharedUser = rs.getString("sharedUser");
				users.add(sharedUser);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return users;
	}
	
	public void addUser(Integer fileID, String toUser) {
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("INSERT INTO FileShare (fileID, sharedUser) VALUES (?, ?)");
			ps.setInt(1, fileID);
			ps.setString(2, toUser);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean removeUser(int fileID, String user) {
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("DELETE FROM FileShare WHERE fileID=? AND sharedUser=?");
			ps.setInt(1, fileID);
			ps.setString(2, user);
			ps.executeUpdate();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void updateFileText(Integer fileID, String text) {
		String filename = getFilename(fileID);
		String owner = getOwner(fileID);
		MeatServerGUI.addMessage("Update Added " + " Owner: " + owner + " File: " + filename);
		Queue<String> updateQueue = fileUpdateQueue.get(fileID);
		if (updateQueue == null){
			updateQueue = new LinkedList<String>();
		}
		updateQueue.offer(text);
		System.out.println("Updating with:" + text);
	}
	
	public String getLatestText(Integer fileID) {
		Lock mLock = new ReentrantLock();
		mLock.lock();
		try {
			String owner = getOwner(fileID);
			String filename = getFilename(fileID);
			//System.out.println("Merging:" + filename);
			File cloudFile = new File(Constants.serverDrivePath + owner + "/" + fileID);
			if(cloudFile.exists()) {
				FileInputStream fis;			
				fis = new FileInputStream(cloudFile);
				byte[] data = new byte[(int) cloudFile.length()];
				fis.read(data);
				fis.close();
				String originalText = new String(data, "UTF-8");
				diff_match_patch dmp = new diff_match_patch();
				Queue<String> updates = fileUpdateQueue.get(fileID);
				System.out.println("Original Text: " + originalText);
				if(updates == null) {
					updates = new LinkedList<String>();
				}
				String tempText = new String(data, "UTF-8");
				LinkedList<Patch> patch = null;
				while(!updates.isEmpty()) {
					patch = dmp.patch_make(originalText, updates.poll());
					tempText = (String)(dmp.patch_apply(patch, tempText)[0]);
				}
				if(!tempText.equals(originalText)){
					originalText = tempText;
					System.out.println("Latest Text: " + tempText);
					cloudFile.delete();
					cloudFile = new File(Constants.serverDrivePath + owner + "/" + fileID);
					FileWriter fw = new FileWriter(cloudFile);
					fw.write(tempText);
					fw.close();
					MeatServerGUI.addMessage("File Updated " + " Owner: " + owner + " File: " + filename);
				}
				return originalText;
			}
			mLock.notifyAll();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mLock.notifyAll();
		} catch (IOException e) {
			e.printStackTrace();
			mLock.notifyAll();
		}
		mLock.notifyAll();
		return null;
	}
	
	public boolean isShared(Integer fileID, String username) {
		Boolean shared = false;
		try {
			PreparedStatement ps = databaseConnection.prepareStatement("SELECT * FROM Files WHERE fileID=? AND owner=?");
			ps.setInt(1, fileID);
			ps.setString(2, username);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				shared = true;
			}
			
			ps = databaseConnection.prepareStatement("SELECT * FROM FileShare WHERE fileID=? AND sharedUser=?");
			ps.setInt(1, fileID);
			ps.setString(2, username);
			rs = ps.executeQuery();
			if (rs.next()){
				shared = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return shared;
	}
	
		
	public static void main(String [] args) {
		FileInputStream input = null;
		Properties prop = new Properties();
		try {
			input = new FileInputStream(Constants.resourcePath + Constants.serverConfigFilePath);
			prop.load(input);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		int port = Integer.parseInt(prop.getProperty("port"));
		new MeatServer(port);
	}
}
