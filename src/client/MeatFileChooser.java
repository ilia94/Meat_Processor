package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class MeatFileChooser extends JFrame implements ListSelectionListener {
	private static final long serialVersionUID = 1229217267917490022L;
	private JList<String> fileList;
	private JTextField fileNameField;
	private DefaultListModel<String> listModel;
	private Vector<OpenFile> files;
	private int mode;
	private MeatButton OpenSaveButton;
	private String selectedFilename;
	private JScrollPane listScrollPane;
	private MeatClientGUI client;
	private MeatProcessorPanelGUI processor;
	private MeatFileChooser cur;
	private String owner;
	
	public MeatFileChooser(MeatClientGUI meatClientGUI, MeatProcessorPanelGUI mpp, String owner, Vector<OpenFile> files, int action) {
		this.processor = mpp;
		this.client = meatClientGUI;
		this.mode = action;
		this.owner = owner;
		this.files = files;
		instantiateComponents();
		createGUI();
		addActions();
		cur = this;
	}
	
	private void instantiateComponents() {
		listModel = new DefaultListModel<String>();
		if(files != null){
			for (OpenFile file : files) {
				listModel.addElement(file.getFilename());
			}
		}
		fileList = new JList<String>(listModel);
		fileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listScrollPane = new JScrollPane(fileList);
        OpenSaveButton = new MeatButton("Open");
        fileNameField = new JTextField(10);
	}
	
	private void createGUI() {
		fileList.setVisibleRowCount(5);
		JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(fileNameField);
        buttonPane.add(Box.createHorizontalStrut(5));
        buttonPane.add(OpenSaveButton);
        buttonPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		add(listScrollPane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.PAGE_END);
        setSize(400, 300);
        setVisible(true);
	}
	
	private void addActions() {
		fileList.addListSelectionListener(this);
		if(mode == 1) {
			OpenSaveButton.setText("Open");
			fileNameField.setEditable(false);
			OpenSaveButton.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e) {
					String filename = fileList.getSelectedValue();
					int index = fileList.getSelectedIndex();
					if (filename != null) {
						Integer fileID = files.elementAt(index).getFileID();
						client.openOnlineFile(owner, fileID);
						cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
					} else {
						JOptionPane.showMessageDialog(client,
							    "Please Select a File",
							    "Open Error",
							    JOptionPane.WARNING_MESSAGE);
					}
				}
			});
			
		} else {
			OpenSaveButton.setText("Save");
			OpenSaveButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if (fileNameField.getText().length() != 0) {
						String filename = fileNameField.getText();
						String saveText = processor.getTextToSave();
						Integer newFileID = client.saveOnlineFile(filename, saveText);
						cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
						if (newFileID != null) {
							JOptionPane.showMessageDialog(client,
								    "File Succefully Saved",
								    "Save Success",
								    JOptionPane.INFORMATION_MESSAGE);
							processor.savedOnline(filename, newFileID);
						} else {
							JOptionPane.showMessageDialog(client,
								    "Failed to Save File",
								    "Save Error",
								    JOptionPane.WARNING_MESSAGE);
						}cur.dispatchEvent(new WindowEvent(cur, WindowEvent.WINDOW_CLOSING));
					} else {
						JOptionPane.showMessageDialog(client,
							    "Please Enter a File Name",
							    "Error",
							    JOptionPane.WARNING_MESSAGE);
					}
				}
				
			});
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectedFilename = fileList.getSelectedValue();
		fileNameField.setText(selectedFilename);
	}
}