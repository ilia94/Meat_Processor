package client;

import java.util.ArrayList;

public class UpdateThread extends Thread{
	private MeatClientGUI client;
	@SuppressWarnings("unused")
	private MeatProcessorPanelGUI processor;
	private ArrayList<OpenFile> openFiles;
	
	public UpdateThread(MeatClientGUI client, MeatProcessorPanelGUI processor) {
		this.client = client;
		this.processor = processor;
		this.openFiles = processor.openFiles;
		
	}
	
	public void run() {
			try{
				while(true) {
					Thread.sleep(client.updateInterval);
					for(int i = 0; i<openFiles.size(); i++) {
						if (openFiles.get(i).isOnline()) {
							client.pullUpdates(openFiles.get(i), i);
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}
}
