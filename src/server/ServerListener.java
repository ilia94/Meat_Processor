package server;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import Utilities.Constants;
import network.NetworkData;

public class ServerListener extends Thread {
	private ServerSocket ss;
	private Vector<ServerThread> clientConnections;
	private volatile boolean Run;
	private MeatServer server;
	
	public ServerListener(MeatServer meatServer, ServerSocket socket) {
		server = meatServer;
		ss = socket;
		clientConnections = new Vector<ServerThread>();
		Run = true;
	}
	
	public void removeClientConnection(ServerThread ccm) {
		clientConnections.remove(ccm);
	}
	
	public void notifyAllClient(NetworkData data) {
		for(ServerThread st : clientConnections) {
			try {
				st.oos.writeObject(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		try {
			MeatServerGUI.addMessage(Constants.initialServerMessage + ss.getLocalPort());
			while(Run) {
				Run = server.isRunning();
				Socket socket = ss.accept();
				try {
					ServerThread ccm = new ServerThread(server, socket);
					ccm.start();
					clientConnections.add(ccm);
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			}
		} catch(BindException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} finally {
			for (ServerThread thread : clientConnections) {
				thread.closeSocket();
			}
			MeatServerGUI.addMessage(Constants.endServerMessage);
		}
	}
}
