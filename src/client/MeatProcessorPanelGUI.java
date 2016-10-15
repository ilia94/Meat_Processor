package client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
//import javax.swing.event.UndoableEditEvent;
//import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyledDocument;
//import javax.swing.undo.CannotUndoException;
//import javax.swing.undo.UndoManager;
import com.apple.eawt.Application;
import Utilities.Constants;
import labkovsk_CSCI201L_Assignment1.ErrorScanner;
import labkovsk_CSCI201L_Assignment1.Suggestor;

public class MeatProcessorPanelGUI extends JPanel{
	public static final long serialVersionUID = 69;
	protected static final String ENCODING = null;
	private JMenuBar menuBar;
	private JPanel tabPanel;
	private JMenu fileMenu, editMenu, spellCheckMenu, userMenu;
	private MeatMenuItem newFileButton, openFileButton, saveFileButton, closeFileButton;
	//private MeatMenuItem undoButton, redoButton;
	private MeatMenuItem cutButton, copyButton, pasteButton, selectAllButton;
	private MeatMenuItem runSpellCheckButton, configureSpellCheckButton, addUserButton, removeUserButton;
	protected MeatTabbedPane fileTabbedPane;
	protected ArrayList<OpenFile> openFiles;
	//private ArrayList<UndoManager> undoManagers;
	private String clipboard;
	private String RUNPANEL = "The Run Panel Component";
	private String CONFIGUREPANEL = "The Configure Panel Component";
	private BufferedImage dockIcon;
	private Application application;
	private MeatClientGUI client;
	private Boolean online;
	private MeatProcessorPanelGUI cur;
	
	public MeatProcessorPanelGUI(MeatClientGUI mcg, boolean o) {
		online = o;
		client = mcg;
		cur = this;
		instantiateComponents();
		createGUI();
		addActions();
		if(online){
			(new UpdateThread(client, cur)).start();
		}
		
	}
	
	private void instantiateComponents() {
		application = Application.getApplication();
		menuBar = new JMenuBar(){
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
			  Image menuBarImage = Toolkit.getDefaultToolkit().getImage("resources/img/menu/red_button11.png");
			  g.drawImage(menuBarImage, 0, 0, this.getWidth(), this.getHeight(), 0, 0, menuBarImage.getWidth(this), menuBarImage.getHeight(this), this);
			  }
		};
		tabPanel = new JPanel();
		tabPanel.setLayout(new BorderLayout());
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		spellCheckMenu = new JMenu("Spell Check");
		userMenu = new JMenu("Users");
		newFileButton = new MeatMenuItem("New");
		openFileButton = new MeatMenuItem("Open");
		saveFileButton = new MeatMenuItem("Save");
		closeFileButton = new MeatMenuItem("Close");
		//undoButton = new MeatMenuItem("Undo");
		//redoButton = new MeatMenuItem("Redo");
		cutButton = new MeatMenuItem("Cut");
		copyButton = new MeatMenuItem("Copy");
		pasteButton = new MeatMenuItem("Paste");
		selectAllButton = new MeatMenuItem("Select All");
		runSpellCheckButton = new MeatMenuItem("Run");
		configureSpellCheckButton = new MeatMenuItem("Configure");
		addUserButton = new MeatMenuItem("Add User");
		removeUserButton = new MeatMenuItem("Remove User");
		fileTabbedPane = new MeatTabbedPane();
		openFiles = new ArrayList<OpenFile>();
		//undoManagers = new ArrayList<UndoManager>();
		clipboard = new String();
		try {
			dockIcon = ImageIO.read(new File("resources/img/icon/office.png/"));
		} catch (IOException e) {}
		if(!online) {
			userMenu.setVisible(false);
		}
	}
	
	private void createGUI() {
		setLayout(new BorderLayout());
		application.setDockIconImage(dockIcon);
		setSize(1000, 700);
		setLocation(200, 100);
		fileMenu.setFont(Constants.myFont);
		fileMenu.setForeground(Color.WHITE);
		fileMenu.add(newFileButton);
		fileMenu.add(openFileButton);
		fileMenu.add(saveFileButton);
		fileMenu.add(closeFileButton);
		editMenu.setForeground(Color.WHITE);
		editMenu.setFont(Constants.myFont);
		//editMenu.add(undoButton);
		//editMenu.add(redoButton);
		editMenu.addSeparator();
		editMenu.add(cutButton);
		editMenu.add(copyButton);
		editMenu.add(pasteButton);
		editMenu.addSeparator();
		editMenu.add(selectAllButton);
		spellCheckMenu.setForeground(Color.WHITE);
		spellCheckMenu.setFont(Constants.myFont);
		spellCheckMenu.add(runSpellCheckButton);
		spellCheckMenu.add(configureSpellCheckButton);
		userMenu.setForeground(Color.WHITE);
		userMenu.setFont(Constants.myFont);
		userMenu.add(addUserButton);
		userMenu.add(removeUserButton);
		addUserButton.setEnabled(false);
		removeUserButton.setEnabled(false);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(spellCheckMenu);
		menuBar.add(userMenu);
		client.setJMenuBar(menuBar);
		tabPanel.add(fileTabbedPane, BorderLayout.CENTER);
		add(tabPanel, BorderLayout.CENTER);
		saveFileButton.setEnabled(false);
		setVisible(true);
		
	}
	
	private void addActions() {
		
		//create a new file
		newFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OpenFile newFile = new OpenFile("File " + (openFiles.size()+1), client.getUsername());
				openFiles.add(newFile);
				displayFile(newFile, "");
				/*
				JScrollPane scroll = new JScrollPane(newTextPane);
				scroll.getVerticalScrollBar().setUI(new MeatScrollBarUI());
				JPanel DocPanel = new JPanel();
				DocPanel.setLayout(new BorderLayout());
				DocPanel.setName("File "+openFiles.size());
				DocPanel.add(scroll, BorderLayout.CENTER);
				latestNewFile++;
				fileTabbedPane.add("File "+latestNewFile, DocPanel);
				fileTabbedPane.setSelectedComponent(DocPanel);
				newTextPane.requestFocus();
				if (fileTabbedPane.getSelectedComponent() != null) {
					saveFileButton.setEnabled(true);
				}
				
				
				UndoManager undoManager = new UndoManager();
				undoManagers.add(undoManager);
				(newTextPane.getDocument()).addUndoableEditListener(new UndoableEditListener() {
					public void undoableEditHappened(UndoableEditEvent e) {
						undoManager.addEdit(e.getEdit());
						if (undoManager.canUndo()) undoButton.setEnabled(true);
						else undoButton.setEnabled(false);
						if (undoManager.canRedo()) redoButton.setEnabled(true);
						else redoButton.setEnabled(false);
					}
				});
				*/
			}
		});
		newFileButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//open a file
		openFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(online) {
					String[] options = {"Online",
		                    "Offline", "Cancel"};
					int chosen = JOptionPane.showOptionDialog(client,
						    "Open From?",
						    "Open",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
					if (chosen == JOptionPane.YES_OPTION){
						HashMap<String,Vector<OpenFile>> fileMap = client.getAccesibleFiles();
						@SuppressWarnings("unused")
						MeatUserChooser chooser = new MeatUserChooser(client, cur, fileMap, null, 1);
					}
					else if (chosen == JOptionPane.NO_OPTION){
						JFileChooser openChooser = new JFileChooser();
						FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt Files", "txt");
						openChooser.setFileFilter(filter);
						if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
							File selectedFile = openChooser.getSelectedFile();
							String text = readText(selectedFile);
							OpenFile newFile = new OpenFile(selectedFile.getName(), client.getUsername());
							newFile.setOffline(selectedFile.getName(), selectedFile.getPath());
							openFiles.add(newFile);
							displayFile(newFile, text);
						}
					}
				}
				else{
					JFileChooser openChooser = new JFileChooser();
					FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt Files", "txt");
					openChooser.setFileFilter(filter);
					if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
						File selectedFile = openChooser.getSelectedFile();
						String text = readText(selectedFile);
						OpenFile newFile = new OpenFile(selectedFile.getName(), client.getUsername());
						newFile.setOffline(selectedFile.getName(), selectedFile.getPath());
						openFiles.add(newFile);
						displayFile(newFile, text);
					}
				}
					
			}
		});
		
		openFileButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//save the currently opened file
		saveFileButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (online) {
					String[] options = {"Online",
		                    "Offline", "Cancel"};
					int chosen = JOptionPane.showOptionDialog(client,
						    "Save To?",
						    "Save",
						    JOptionPane.YES_NO_CANCEL_OPTION,
						    JOptionPane.QUESTION_MESSAGE,
						    null,
						    options,
						    options[2]);
					if (chosen == JOptionPane.YES_OPTION){
						HashMap<String,Vector<OpenFile>> fileMap = client.getAccesibleFiles();
						Vector<OpenFile> files = fileMap.get(client.getUsername());
						@SuppressWarnings("unused")
						MeatFileChooser chooser = new MeatFileChooser(client, cur, client.getUsername(), files, 2);
					}
					else if (chosen == JOptionPane.NO_OPTION){
						saveOffline();
					}
				}
				else {
					saveOffline();
				}
				
			}
		});
		saveFileButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//close selected file
		closeFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = fileTabbedPane.getSelectedIndex();
				if(!openFiles.isEmpty()){
					openFiles.get(index).setClosed();
					openFiles.remove(index);
					//undoManagers.remove(index);
					fileTabbedPane.getComponentAt(index);
					fileTabbedPane.remove(index);
					
				}
				if (fileTabbedPane.getSelectedComponent() == null) {
					saveFileButton.setEnabled(false);
				}
			}
		});
		
		//undo most recent change made
		/*undoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = fileTabbedPane.getSelectedIndex();
				UndoManager undoManager = undoManagers.get(index);
				try {undoManager.undo();}
			    catch (CannotUndoException ex) {System.out.println("Cannot Undo");}
				if (undoManager.canUndo()) undoButton.setEnabled(true);
				else undoButton.setEnabled(false);
				if (undoManager.canRedo()) redoButton.setEnabled(true);
				else redoButton.setEnabled(false);
			}
		});
		undoButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//redo most recent undone change made
		redoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = fileTabbedPane.getSelectedIndex();
				UndoManager undoManager = undoManagers.get(index);
				try {undoManager.redo();}
			    catch (CannotUndoException ex) {System.out.println("Cannot Redo");}
				if (undoManager.canUndo()) undoButton.setEnabled(true);
				else undoButton.setEnabled(false);
				if (undoManager.canRedo()) redoButton.setEnabled(true);
				else redoButton.setEnabled(false);
			}
		});
		redoButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, (java.awt.event.InputEvent.SHIFT_MASK | (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask())))
				);
		*/
		//copy selection to clipboard and delete selection
		cutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
				BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
				JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
				JViewport vp = selectedScrollPane.getViewport();
				MeatTextPane selectedTextPane = (MeatTextPane) vp.getView();
				clipboard = selectedTextPane.getSelectedText();
				selectedTextPane.replaceSelection("");
			}
		});
		cutButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_X, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//copy selection to clipboard
		copyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
				BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
				JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
				JViewport vp = selectedScrollPane.getViewport();
				MeatTextPane selectedTextPane = (MeatTextPane) vp.getView();
				clipboard = selectedTextPane.getSelectedText();
			}
		});
		copyButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_C, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//replace selection with clipboard contents
		pasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
				BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
				JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
				JViewport vp = selectedScrollPane.getViewport();
				MeatTextPane selectedTextPane = (MeatTextPane) vp.getView();
				selectedTextPane.replaceSelection(clipboard);
			}
		});
		pasteButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_V, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//selects all text in the current tab
		selectAllButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
				BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
				JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
				JViewport vp = selectedScrollPane.getViewport();
				MeatTextPane selectedTextPane = (MeatTextPane) vp.getView();
				selectedTextPane.selectAll();
			}
		});
		selectAllButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_A, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//runs spell check
		runSpellCheckButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileTabbedPane.getSelectedComponent() != null){
					JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
					BorderLayout currentDocLayout = (BorderLayout) currentDoc.getLayout();
					JScrollPane selectedScrollPane = (JScrollPane) currentDocLayout.getLayoutComponent(BorderLayout.CENTER);
					JViewport vp = selectedScrollPane.getViewport();
					MeatTextPane currentTextPane = (MeatTextPane) vp.getView();
					if (currentDocLayout.getLayoutComponent(BorderLayout.EAST) == null) {
						SpellCheckPane currentSpellCheckPane = new SpellCheckPane(currentTextPane, RUNPANEL);
						currentSpellCheckPane.setVisible(true);
						currentDoc.add(currentSpellCheckPane, BorderLayout.EAST);
					} else {
						SpellCheckPane currentSpellCheckPane = (SpellCheckPane) currentDocLayout.getLayoutComponent(BorderLayout.EAST);
						currentSpellCheckPane.runSpellCheck(currentTextPane);
					}
					currentDoc.revalidate();
					currentDoc.repaint();
				}
			}
		});
		runSpellCheckButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0)
				);
		
		//brings up configuration options for spell check
		configureSpellCheckButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (fileTabbedPane.getSelectedComponent() != null){
					JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
					BorderLayout currentDocLayout = (BorderLayout) currentDoc.getLayout();
					JScrollPane selectedScrollPane = (JScrollPane) currentDocLayout.getLayoutComponent(BorderLayout.CENTER);
					JViewport vp = selectedScrollPane.getViewport();
					MeatTextPane currentTextPane = (MeatTextPane) vp.getView();
					if (currentDocLayout.getLayoutComponent(BorderLayout.EAST) == null){
						SpellCheckPane currentSpellCheckPane = new SpellCheckPane(currentTextPane, CONFIGUREPANEL);
						currentSpellCheckPane.setVisible(true);
						currentDoc.add(currentSpellCheckPane, BorderLayout.EAST);
					} else {
						SpellCheckPane currentSpellCheckPane = (SpellCheckPane) currentDocLayout.getLayoutComponent(BorderLayout.EAST);
						currentSpellCheckPane.configureSpellCheck();
						
					}
					currentDoc.revalidate();
					currentDoc.repaint();
				}
			}
		});
		
		addUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String toUser = (String)JOptionPane.showInputDialog(
	                   "Add User:");
				Integer fileID = openFiles.get(fileTabbedPane.getSelectedIndex()).getFileID();
				if ((toUser != null) && (toUser.length() > 0)) {
					client.addUser(fileID, toUser);
				}
			}
		});
		
		removeUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OpenFile currentFile = openFiles.get(fileTabbedPane.getSelectedIndex());
				Integer fileID = currentFile.getFileID();
				Vector<String> sharedUsers = client.getSharedUsers(fileID);
				HashMap<String,Vector<OpenFile>> sharedUserMap = new HashMap<String, Vector<OpenFile>>();
				for(String user: sharedUsers) {
					sharedUserMap.put(user, new Vector<OpenFile>());
				}
				@SuppressWarnings("unused")
				MeatUserChooser remover = new MeatUserChooser(client, cur, sharedUserMap, fileID, 2);
			}
		});
		
		fileTabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				if(!online){
					userMenu.setVisible(false);
				} else {
					if(fileTabbedPane.getSelectedIndex() == -1) {
						addUserButton.setEnabled(false);
						removeUserButton.setEnabled(false);
					} else if(openFiles.isEmpty()) {
						addUserButton.setEnabled(false);
						removeUserButton.setEnabled(false);
					} else if(openFiles.get(fileTabbedPane.getSelectedIndex()).getOwner().equals(client.getUsername()) && openFiles.get(fileTabbedPane.getSelectedIndex()).isOnline()) {
						addUserButton.setEnabled(true);
						removeUserButton.setEnabled(true);
					} else {
						addUserButton.setEnabled(false);
						removeUserButton.setEnabled(false);
					}
				}
			}
		});
	}
	
	private void saveOffline() {
		JFileChooser saveChooser = new JFileChooser(){
			private static final long serialVersionUID = 70;
			public void approveSelection() {
				File f = getSelectedFile();
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
		            int result = JOptionPane.showConfirmDialog(this,f.getName()+" already exists"+"\n"+"Do you want to replace it?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		                case JOptionPane.CANCEL_OPTION:
		                    cancelSelection();
		                    return;
		            }
		        }
		        super.approveSelection();
			}
		};
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt Files", "txt");
		saveChooser.setFileFilter(filter);
		
		//suggest save location if the file already exists
		if(openFiles.get(fileTabbedPane.getSelectedIndex()).isSaved()){
			//change to: make new File() with a pathname and pass it into setSelectedFile below
			File newFile = new File(openFiles.get(fileTabbedPane.getSelectedIndex()).getFilepath());
			saveChooser.setSelectedFile(newFile);
		}
		
		//save the file if a location is selected
		if (saveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			File selectedFile = saveChooser.getSelectedFile();
			try(FileWriter fw = new FileWriter(selectedFile)) {
				fw.write(getTextToSave());
				openFiles.get(fileTabbedPane.getSelectedIndex()).setOffline(selectedFile.getName(), selectedFile.getPath());
				fileTabbedPane.setTitleAt(fileTabbedPane.getSelectedIndex(), selectedFile.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected String getTextToSave() {
		JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
		BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
		JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
		JViewport vp = selectedScrollPane.getViewport();
		MeatTextPane selectedTextPane = (MeatTextPane) vp.getView();
		return selectedTextPane.getText();
	}
	
	protected void savedOnline(String filename, Integer fileID) {
		openFiles.get(fileTabbedPane.getSelectedIndex()).setOnline(filename, fileID);
		fileTabbedPane.setTitleAt(fileTabbedPane.getSelectedIndex(), filename);
		//userMenu.setVisible(true);
		addUserButton.setEnabled(true);
		removeUserButton.setEnabled(true);
	}
	
	
	protected String readText(File selectedFile) {
		String text = null;
		try(BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    text = sb.toString();
		    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	protected void displayFile(OpenFile file, String text){
		MeatTextPane openTextPane = new MeatTextPane(client, file);
		openTextPane.setText(text);
		JScrollPane scroll = new JScrollPane(openTextPane);
	    scroll.getVerticalScrollBar().setUI(new MeatScrollBarUI());
	    JPanel DocPanel = new JPanel();
	    DocPanel.setLayout(new BorderLayout());
		DocPanel.add(scroll, BorderLayout.CENTER);
		DocPanel.setName(file.getFilename());
	    fileTabbedPane.add(DocPanel);
	    fileTabbedPane.setSelectedComponent(DocPanel);
	    openTextPane.requestFocus();
	  
	    if (fileTabbedPane.getSelectedComponent() != null) {
			saveFileButton.setEnabled(true);
		}
	    if(!online){
	    	userMenu.setEnabled(false);
	    } else if(!openFiles.get(fileTabbedPane.getSelectedIndex()).getOwner().equals(client.getUsername())) {
	    	addUserButton.setEnabled(false);
	    	removeUserButton.setEnabled(false);
	    }
	    /*UndoManager undoManager = new UndoManager();
	    (openTextPane.getDocument()).addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent e) {
				undoManager.addEdit(e.getEdit());
				if (undoManager.canUndo()) undoButton.setEnabled(true);
				else undoButton.setEnabled(false);
				if (undoManager.canRedo()) redoButton.setEnabled(true);
				else redoButton.setEnabled(false);
			}
		});
		undoManagers.add(undoManager);
		*/
	}

class SpellCheckPane extends JPanel {
	
	private static final long serialVersionUID = 23;
	private String RUNPANEL = "The Run Panel Component";
	private String CONFIGUREPANEL = "The Configure Panel Component";
	private JPanel cardPanel;
	private RunPanel runPanel;
	private ConfigurePanel configurePanel;
	private MeatButton closeButton;
	private CardLayout cardLayout;
	private File wordListFile, keyboardFile;
	private String defaultWordListPath = "resources/wordlist.wl";
	private String defaultKeyboardFilePath = "resources/qwerty-us.kb";
	private MeatTextPane currentTextPane;
	
	SpellCheckPane(MeatTextPane textPane, String panel) {
		
		currentTextPane = textPane;
		instantiateComponents();
		createGUI();
		addActions();
		cardLayout.show(cardPanel, panel);
		cardPanel.revalidate();
		cardPanel.repaint();
	}

	public void runSpellCheck(MeatTextPane textPane) {
		wordListFile = configurePanel.wordListFile;
		keyboardFile = configurePanel.keyboardFile;
		currentTextPane = textPane;
		cardPanel.remove(runPanel);
		runPanel = new RunPanel(currentTextPane, wordListFile, keyboardFile);
		cardPanel.add(runPanel, RUNPANEL);
		cardLayout.show(cardPanel, RUNPANEL);
		cardPanel.revalidate();
		cardPanel.repaint();
		setVisible(true);
	}

	void configureSpellCheck() {
		cardLayout.show(cardPanel, CONFIGUREPANEL);
		cardPanel.revalidate();
		cardPanel.repaint();
		setVisible(true);
	}
	
	void setWL(File WLFILE) {
		wordListFile = WLFILE;
	}
	
	void setKB(File KBFILE) {
		keyboardFile = KBFILE;
	}

	void instantiateComponents() {
		cardLayout = new CardLayout();
		closeButton = new MeatButton("Close");
		cardPanel = new JPanel();
		wordListFile = new File(defaultWordListPath);
		keyboardFile = new File(defaultKeyboardFilePath);
		runPanel = new RunPanel(currentTextPane, wordListFile, keyboardFile);
		configurePanel = new ConfigurePanel();
	}
	
	void createGUI() {
		setLayout(new BorderLayout());
		cardPanel.setLayout(cardLayout);
		cardPanel.add(runPanel, RUNPANEL);
		cardPanel.add(configurePanel, CONFIGUREPANEL);
		add(cardPanel, BorderLayout.NORTH);
		closeButton.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		add(closeButton, BorderLayout.SOUTH);
	}
	
	void addActions() {
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				currentTextPane.getHighlighter().removeAllHighlights();
			}
		});
	}
	
}

class RunPanel extends JPanel {
	private static final long serialVersionUID = 24;
	private boolean errorsExist;
	private String cError;
	private MeatLabel currentWord;
	private MeatButton ignoreButton, addButton, changeButton;
	private JComboBox<String> currentOptions;
	private GroupLayout runPanelLayout;
	private String[] textWords;
	private File wordListFile;
	private File keyboardFile;
	private ErrorScanner errorScanner;
	private Suggestor suggestor;
	private Vector<String> errors;
	private Vector<String> unformattedErrors;
	private Vector<Vector<String>> suggestions;
	int errorIndex;
	private String selectedOption;
	private MeatTextPane currentTextPane;
	private String text;
	private int offset;
	private int offsetEnd;
	private Image background;
	
	RunPanel(MeatTextPane textPane, File wlFile, File kbFile) {
		currentTextPane = textPane;
		wordListFile = wlFile;
		keyboardFile = kbFile;
		instantiateComponents();
		createGUI();
		addActions();
		nextError();
	}
	
	@Override
	protected void paintComponent(Graphics g) {

	    super.paintComponent(g);
	        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, background.getWidth(this), background.getHeight(this), this);
	}
	
	void instantiateComponents() {
		errorIndex = 0;
		offset = 0;
		offsetEnd = 0;
		text = currentTextPane.getText();
		textWords = text.split("\\s+");
		errorScanner = new ErrorScanner(textWords, wordListFile);
		errors = errorScanner.scan();
		unformattedErrors = errorScanner.noFormatList();
		suggestor = new Suggestor(errorScanner, keyboardFile);
		suggestions = suggestor.generateSuggestions();
		runPanelLayout = new GroupLayout(this);
		currentWord = new MeatLabel("No Mispelled Words");
		ignoreButton = new MeatButton("Ignore");
		addButton = new MeatButton("Add");
		changeButton = new MeatButton("Change");
		currentOptions = new JComboBox<String>();
		currentOptions.setUI(new MeatComboBoxUI());
		currentOptions.setFont(Constants.myFont);
		try {
			background = ImageIO.read(new File("resources/img/backgrounds/darkgrey_panel.png/"));
		} catch (IOException e) {}
	}
	
	void nextError() {
		currentTextPane.getHighlighter().removeAllHighlights();
		if (errorIndex < errors.size() && errors.get(errorIndex).length() > 0) {
			errorsExist = true;
			cError = errors.get(errorIndex);
			String word = unformattedErrors.get(errorIndex);
			currentWord.setText(word);
			currentOptions.removeAllItems();
			if (!suggestions.get(errorIndex).isEmpty()) {
				selectedOption = suggestions.get(errorIndex).get(0);
				for (String suggestion : suggestions.get(errorIndex)) {
					currentOptions.addItem(suggestion);
				}
				changeButton.setEnabled(true);
			}
			else {
				changeButton.setEnabled(false);
				selectedOption = null;
			}
			for (int i = offsetEnd; i < text.length()-word.length()+1; i++) {
				if (text.substring(i, i+word.length()).equals(word)) {
					offset = i;
					offsetEnd = i+word.length();
					break;
				}
			}
			try {
				currentTextPane.getHighlighter().addHighlight(offset, offset+cError.length(), 
						new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else {
			errorsExist = false;
			cError = null;
			currentWord.setText("No Errors");
			ignoreButton.setEnabled(false);
			addButton.setEnabled(false);
			changeButton.setEnabled(false);
			selectedOption = null;
			if (errors.size() > 0 && errors.get(0).length() > 0)
				getParent().getParent().setVisible(false);
		}
	}
	
	void createGUI() {
		this.setBorder(BorderFactory.createTitledBorder(null,
                "Spell Check",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                Constants.myFont,
                Color.WHITE));
		setLayout(runPanelLayout);
		runPanelLayout.setAutoCreateGaps(true);
		runPanelLayout.setVerticalGroup(
				runPanelLayout.createSequentialGroup()
				.addComponent(currentWord)
				.addGroup(runPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(ignoreButton)
						.addComponent(addButton))
				.addGroup(runPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(currentOptions)
						.addComponent(changeButton))
				);
		runPanelLayout.setHorizontalGroup(
				runPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(currentWord)
						.addGroup(runPanelLayout.createSequentialGroup()
								.addComponent(ignoreButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(addButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(runPanelLayout.createSequentialGroup()
								.addComponent(currentOptions, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(changeButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						);
	}
	
	void addActions() {
		ignoreButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentTextPane.getHighlighter().removeAllHighlights();
				errorIndex++;
				nextError();	
			}
		});
		
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				BufferedWriter bw = null;
				try {
					bw = new BufferedWriter(new FileWriter(wordListFile, true));
					bw.newLine();
					bw.write(cError);
					bw.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (bw != null)
						try {
							bw.close();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
				}
				errorIndex++;
				nextError();
			}
		});
		
		currentOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedOption = (String)currentOptions.getSelectedItem();
			}
		});
		
		changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(errorsExist) {
					StyledDocument doc = currentTextPane.getStyledDocument();
					try {
						doc.remove(offset, cError.length());
						doc.insertString(offset, selectedOption, null);
						text = doc.getText(0, doc.getLength());
						currentTextPane.getHighlighter().removeAllHighlights();
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
					errorIndex++;
					nextError();
				}
			}
		});
	}
}

class ConfigurePanel extends JPanel {

	private static final long serialVersionUID = 8;
	private MeatLabel wordListName, keyboardName;
	private MeatButton selectWordListButton, selectKeyboardButton;
	private String defaultWordListPath = "resources/wordlist.wl";
	private String defaultKeyboardFilePath = "resources/qwerty-us.kb";
	protected File wordListFile, keyboardFile;
	private Image background;
	
	
	ConfigurePanel() {
		instantiateComponents();
		createGUI();
		addActions();
	}
	
	void instantiateComponents() {
		wordListFile = new File(defaultWordListPath);
		keyboardFile = new File(defaultKeyboardFilePath);
		wordListName = new MeatLabel(wordListFile.getName());
		keyboardName = new MeatLabel(keyboardFile.getName());
		selectWordListButton = new MeatButton("Select Word List");
		selectKeyboardButton = new MeatButton("Select Kayboard Layout");
		try {
			background = ImageIO.read(new File("resources/img/backgrounds/darkgrey_panel.png/"));
		} catch (IOException e) {}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), 0, 0, background.getWidth(this), background.getHeight(this), this);
	}
	
	void createGUI() {
		this.setBorder(BorderFactory.createTitledBorder(null,
                "Configure",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                Constants.myFont,
                Color.WHITE));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(wordListName);
		add(selectWordListButton);
		add(keyboardName);
		add(selectKeyboardButton);
	}
	
	void addActions() {
		selectWordListButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".wl Files", "wl");
				openChooser.setFileFilter(filter);
				File selectedFile = null;
				if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					selectedFile = openChooser.getSelectedFile();
					wordListFile = selectedFile;
					wordListName.setText(wordListFile.getName());
				}
			}
		});
		
		selectKeyboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".kb Files", "kb");
				openChooser.setFileFilter(filter);
				File selectedFile = null;
				if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					selectedFile = openChooser.getSelectedFile();
					keyboardFile = selectedFile;
					keyboardName.setText(keyboardFile.getName());
					}
				}
			});
		}
	}
}