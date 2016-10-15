package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;
import com.apple.eawt.Application;
import Utilities.Constants;
import network.AddUserRequest;
import network.FileInfoRequest;
import network.AccesibleFileRequest;
import network.LoginRequest;
import network.OpenFileRequest;
import network.PullRequest;
import network.RegistrationRequest;
import network.RemoveUserRequest;
import network.SaveFileRequest;
import network.UpdateFileRequest;
import network.UserLookup;
//import network.NetworkData;

public class MeatClientGUI extends JFrame {

	private static final long serialVersionUID = 8807992973338903906L;
	private Socket socket;
	private JPanel cardPanel;
	private StartPanelGUI startPanel;
	private LoginPanelGUI loginPanel;
	private RegistrationPanelGUI resgistrationPanel;
	private MeatProcessorPanelGUI meatProcessorPanel;
	private CardLayout cardLayout;
	private BufferedImage dockIcon;
	public Image background;
	public boolean connectedServer;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private String username;
	protected Integer updateInterval;
	
	
	MeatClientGUI(Socket s, Integer updateInterval) {
		super(Constants.clientGUITitleString);
		socket = s;
		initializeVariables();
		createGUI();
		this.updateInterval = updateInterval;
	}
	
	void initializeVariables() {
		if (socket != null){
			connectedServer = true;
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois =  new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			connectedServer = false;
			oos = null;
			ois = null;
		}
		cardPanel = new JPanel();
		cardLayout = new CardLayout();
		startPanel = new StartPanelGUI(connectedServer, this);
		loginPanel = new LoginPanelGUI(this);
		resgistrationPanel = new RegistrationPanelGUI(this);
		try {
			dockIcon = ImageIO.read(new File("resources/img/icon/office.png/"));
			background = ImageIO.read(new File("resources/img/backgrounds/meat_panel.png/"));
		} catch (IOException e) {}
	}
	
	void createGUI() {
		Application.getApplication().setDockIconImage(dockIcon);
		cardPanel.setLayout(cardLayout);
		cardPanel.add(loginPanel, "LOGIN");
		cardPanel.add(resgistrationPanel, "REG");
		cardPanel.add(startPanel, "START");
		add(cardPanel, BorderLayout.CENTER);
		cardLayout.show(cardPanel, "START");
		this.setSize(Constants.clientGUIWidth, Constants.clientGUIHeight);
		try{
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("resources/img/icon/cursor.png/").getImage(),new Point(0,0),"custom cursor"));
        }catch(Exception e){}
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public String getUsername() {
		return username;
	}
	
	protected void paintComponent(Graphics g) {

	    super.paintComponents(g);
	        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, background.getWidth(this), background.getHeight(this), this);
	}
	
	public void changeCard(String CARD) {
		cardLayout.show(cardPanel, CARD);
	}
	
	public void launch(boolean online) {
		meatProcessorPanel = new MeatProcessorPanelGUI(this, online);
		cardPanel.add(meatProcessorPanel, "MEAT");
		cardLayout.show(cardPanel, "MEAT");
		cardPanel.revalidate();
		cardPanel.repaint();
	}
	
	public RegistrationPanelGUI getRegisterPanel() {
		return resgistrationPanel;
	}
	
	public LoginPanelGUI getLoginPanel() {
		return loginPanel;
	}
	
	public int register(String username, String password) {
		int error = 2;
		try {
			String encryptedPassword = encrypt(password);
			RegistrationRequest registrationRequest = new RegistrationRequest(username, encryptedPassword);
			oos.writeObject(registrationRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof RegistrationRequest) {
					awaitingResponse = false;
					boolean success = ((RegistrationRequest) message).getResult();
					if(success){
						launch(true);
						this.username = ((RegistrationRequest) message).getUsername();
						error = 0;
					}
					else{
						JOptionPane.showMessageDialog(this,
							    "Username is taken, please try again",
							    "Sign Up Error",
							    JOptionPane.WARNING_MESSAGE);
						error = 1;
					}
				}
			}
		} catch (IOException ioe) {
			return 2;
		} catch (ClassNotFoundException e) {
			return 2;
		}
		return error;
	}
	
	public int login(String username, String password) {
		int error = 2;
		try {
			if (socket.isInputShutdown() || socket.isOutputShutdown() || socket.isClosed() || !socket.isConnected()) {
				JOptionPane.showMessageDialog(this,
					    "Log In Failed. Cannot Connect to Server",
					    "Server Error",
					    JOptionPane.WARNING_MESSAGE);
			} else {
				String encryptedPassword = encrypt(password);
				LoginRequest loginRequest = new LoginRequest(username, encryptedPassword);
				oos.writeObject(loginRequest);
				oos.flush();
				boolean awaitingResponse = true;
				while(awaitingResponse) {
					Object message = ois.readObject();
					if (message instanceof LoginRequest) {
						awaitingResponse = false;
						boolean success = ((LoginRequest) message).getResult();
						if(success){
							launch(true);
							this.username = ((LoginRequest) message).getUsername();
							error = 0;
						}
						else{
							JOptionPane.showMessageDialog(this,
								    "Incorrect Credentials. Please Try Again or Sign Up",
								    "Log In Error",
								    JOptionPane.WARNING_MESSAGE);
							error = 1;
						}
					}
				}
			}
			
		} catch (IOException e) {
			return 2;
		} catch (ClassNotFoundException e) {
			return 2;
		}
		return error;
	}
	
	private String encrypt(String unencryptedPassword) {
		MessageDigest md = null;
	    try {
	      md = MessageDigest.getInstance("SHA");
	    } catch(NoSuchAlgorithmException e) {
	      System.out.println(e.getMessage());
	    } try {
	      md.update(unencryptedPassword.getBytes("UTF-8"));
	    } catch(UnsupportedEncodingException e) {
	    	System.out.println(e.getMessage());
	    }
	    byte raw[] = md.digest();
	    String hash = (Base64.getEncoder()).encodeToString(raw);
	    return hash;
	  }
	
	public HashMap<String,Vector<OpenFile>> getAccesibleFiles() {
		HashMap<String,Vector<OpenFile>> fileInfo = null;
		try{
			AccesibleFileRequest fileRequest = new AccesibleFileRequest(username);
			oos.writeObject(fileRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof AccesibleFileRequest){
					awaitingResponse = false;
					fileInfo = ((AccesibleFileRequest) message).getFileInfo();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return fileInfo;
	}
	
	public void openOnlineFile(String owner, Integer fileID) {
		try{
			OpenFileRequest openFileRequest = new OpenFileRequest(username, fileID);
			oos.writeObject(openFileRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof OpenFileRequest){
					awaitingResponse = false;
					File cloudFile = ((OpenFileRequest) message).getFile();
					if (cloudFile == null) {
						JOptionPane.showMessageDialog(this,
							    "Failed to Open File",
							    "Open Error",
							    JOptionPane.WARNING_MESSAGE);
					} else {
						String filename = ((OpenFileRequest) message).getFilename();
						OpenFile newFile = new OpenFile(filename, owner);
						newFile.setOnline(filename, fileID);
						meatProcessorPanel.openFiles.add(newFile);
						String text = meatProcessorPanel.readText(cloudFile);
						meatProcessorPanel.displayFile(newFile, text);
						JOptionPane.showMessageDialog(this,
							    "File Succefully Opened",
							    "Open Success",
							    JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Integer saveOnlineFile(String filename, String text) {
		Integer savedFileID = null;
		try{
			SaveFileRequest saveFileRequest = new SaveFileRequest(username, filename, text);
			oos.writeObject(saveFileRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof SaveFileRequest){
					awaitingResponse = false;
					savedFileID = ((SaveFileRequest) message).getID();
				}
			}
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
		return savedFileID;
	}
	
	public boolean userCheck (String user) {
		boolean checkResponse = false;
		try {
			UserLookup userLookup = new UserLookup(user);
			oos.writeObject(userLookup);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof UserLookup){
					awaitingResponse = false;
					checkResponse = ((UserLookup) message).getResponse();
				}
			}
		} catch (IOException e) {
			return false;
		} catch (ClassNotFoundException e) {
			return false;
		}
		return checkResponse;
	}
	
	public void addUser(int fileID, String toUser) {
		boolean validUser = userCheck(toUser);
		if (validUser) {
			try {
				AddUserRequest addUserRequest = new AddUserRequest(fileID, toUser, username);
				oos.writeObject(addUserRequest);
				oos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(this,
				    "This User does not exist",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public Vector<String> getSharedUsers(Integer fileID) {
		Vector<String> sharedUsers = null;
		try{
			FileInfoRequest fileInfoRequest = new FileInfoRequest(fileID);
			oos.writeObject(fileInfoRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof FileInfoRequest){
					awaitingResponse = false;
					sharedUsers = ((FileInfoRequest) message).getSharedUsers();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sharedUsers;
	}
	
	public void removeUser(Integer fileID, String user) {
		try {
			RemoveUserRequest removeUserRequest = new RemoveUserRequest(fileID, user);
			oos.writeObject(removeUserRequest);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void updateFile(Integer fileID, String text) {
		try {
			UpdateFileRequest updateFileRequest = new UpdateFileRequest(fileID, text);
			oos.writeObject(updateFileRequest);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pullUpdates(OpenFile file, Integer index) {
		try {
			Integer fileID = file.getFileID();
			PullRequest pullRequest = new PullRequest(fileID, username);
			oos.writeObject(pullRequest);
			oos.flush();
			boolean awaitingResponse = true;
			while(awaitingResponse) {
				Object message = ois.readObject();
				if (message instanceof PullRequest){
					awaitingResponse = false;
					String text = ((PullRequest) message).getText();
					boolean isOnline = ((PullRequest) message).isOnline();
					if(!isOnline){
						meatProcessorPanel.openFiles.get(index).setUnshared();
						JOptionPane.showMessageDialog(this,
							    "You have been removed!",
							    "",
							    JOptionPane.WARNING_MESSAGE);
					} else {
						JPanel currentDoc = (JPanel) meatProcessorPanel.fileTabbedPane.getComponentAt(index);
						BorderLayout currentDocLayout = (BorderLayout) currentDoc.getLayout();
						JScrollPane selectedScrollPane = (JScrollPane) currentDocLayout.getLayoutComponent(BorderLayout.CENTER);
						JViewport vp = selectedScrollPane.getViewport();
						MeatTextPane currentTextPane = (MeatTextPane) vp.getView();
						currentTextPane.setText(text);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.put("TabbedPane.selected", Color.RED.brighter());
			UIManager.put("TabbedPane.unselectedBackground", Color.RED.darker());
			UIManager.put("TabbedPane.font", Constants.mySmallFont);
			UIManager.put("MenuItem.acceleratorFont", Constants.mySmallFont);
			UIManager.put("ComboBox.font", Constants.mySmallFont);
		} catch(Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}

		FileInputStream input = null;
		Properties prop = new Properties();
		try {
			input = new FileInputStream(Constants.resourcePath + Constants.clientConfigFilePath);
			prop.load(input);
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		String host = prop.getProperty("host");
		int port = Integer.parseInt(prop.getProperty("port"));
		int updateInterval = Integer.parseInt(prop.getProperty("interval"));
		Socket socket;
		try {
			socket = new Socket(host, port);
			new MeatClientGUI(socket, updateInterval);
		} catch (IOException e1) {
			new MeatClientGUI(null, null);
		}
	}
/*
	@Override
	public void run() {
		try {
			while(socket != null && socket.isConnected()) {
				NetworkData incomingObject = (NetworkData)ois.readObject();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
		}
	}
*/
	
}
