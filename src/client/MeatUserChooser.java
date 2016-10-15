package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import Utilities.Constants;

public class MeatUserChooser extends JFrame {

	private static final long serialVersionUID = -2426460357286730715L;
	private Integer action;
	private DefaultListModel<String> listModel;
	private JList<String> ownerList;
	private Set<String> ownerSet;
	private HashMap<String,Vector<OpenFile>> fileMap;
	private MeatButton myFilesButton;
	private MeatButton selectUserButton;
	private JScrollPane listScrollPane;
	private MeatClientGUI client;
	private MeatProcessorPanelGUI processor;
	private MeatUserChooser cur;
	private Integer fileID;
	public MeatUserChooser(MeatClientGUI mcGUI, MeatProcessorPanelGUI mpp, HashMap<String,Vector<OpenFile>> fileMap, Integer fileID, int action) {
		this.processor = mpp;
		this.client = mcGUI;
		this.fileMap = fileMap;
		this.action = action;
		this.fileID = fileID;
		instantiateComponents();
		createGUI();
		addActions();
		cur = this;
	}
	
	private void instantiateComponents() {
		listModel = new DefaultListModel<String>();
		ownerList = new JList<String>(listModel);
		ownerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScrollPane = new JScrollPane(ownerList);
        myFilesButton = new MeatButton("My Files");
        selectUserButton = new MeatButton("Select User");
        if(fileMap != null) {
			ownerSet = fileMap.keySet();
			for (String owner : ownerSet) {
				if(!owner.equals(client.getUsername()))
					listModel.addElement(owner);
			}
		}
	}
	
	private void createGUI() {
		ownerList.setVisibleRowCount(5);
		JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(myFilesButton);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(selectUserButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        setSize(400, 300);
        setVisible(true);
        if(!action.equals(Constants.openUser)){
        	myFilesButton.setVisible(false);
        	selectUserButton.setText("Remove User");
        }
	}
	
	private void addActions() {
		myFilesButton.addActionListener(new ActionListener() {			

			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedOwner = client.getUsername();
				Vector<OpenFile> selectedFiles = fileMap.get(selectedOwner);
				new MeatFileChooser(client, processor, selectedOwner, selectedFiles, 1);
				cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
			}
			
		});
		
		selectUserButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedUser = ownerList.getSelectedValue();
				if(action.equals(Constants.openUser)) {
					if(selectedUser != null) {
						Vector<OpenFile> selectedFiles = fileMap.get(selectedUser);
						new MeatFileChooser(client, processor, selectedUser, selectedFiles, 1);
						cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
					}
		        } else {
		        	if(selectedUser != null) {
		        		client.removeUser(fileID, selectedUser);
		        		cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
		        	}
		        }
				
				
			}
		});
	}
}
