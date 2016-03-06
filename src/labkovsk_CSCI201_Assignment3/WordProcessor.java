package labkovsk_CSCI201_Assignment3;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import java.awt.Image;
import java.awt.Point;
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
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import com.apple.eawt.Application;

import labkovsk_CSCI201L_Assignment1.ErrorScanner;
import labkovsk_CSCI201L_Assignment1.Suggestor;



public class WordProcessor extends JFrame{
	public static final long serialVersionUID = 69;
	protected static final String ENCODING = null;
	private JMenuBar menuBar;
	private JMenu fileMenu, editMenu, spellCheckMenu;
	private meatMenuItem newFileButton, openFileButton, saveFileButton, closeFileButton;
	private meatMenuItem undoButton, redoButton, cutButton, copyButton, pasteButton, selectAllButton;
	private meatMenuItem runSpellCheckButton, configureSpellCheckButton;
	private MeatTabbedPane fileTabbedPane;
	private int latestNewFile;
	private ArrayList<File> openFiles;
	private ArrayList<UndoManager> undoManagers;
	private String clipboard;
	private String RUNPANEL = "The Run Panel Component";
	private String CONFIGUREPANEL = "The Configure Panel Component";
	private BufferedImage dockIcon;
	private Application application;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch(Exception e) {
			System.out.println("Warning! Cross-platform L&F not used!");
		}
		WordProcessor MeatProcessor = new WordProcessor();
		MeatProcessor.setVisible(true);
	}
	
	public WordProcessor() {
		super("The Meat Processor");
		instantiateComponents();
		createGUI();
		addActions();
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
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		spellCheckMenu = new JMenu("Spell Check");
		newFileButton = new meatMenuItem("New");
		openFileButton = new meatMenuItem("Open");
		saveFileButton = new meatMenuItem("Save");
		closeFileButton = new meatMenuItem("Close");
		undoButton = new meatMenuItem("Undo");
		redoButton = new meatMenuItem("Redo");
		cutButton = new meatMenuItem("Cut");
		copyButton = new meatMenuItem("Copy");
		pasteButton = new meatMenuItem("Paste");
		selectAllButton = new meatMenuItem("Select All");
		runSpellCheckButton = new meatMenuItem("Run");
		configureSpellCheckButton = new meatMenuItem("Configure");
		fileTabbedPane = new MeatTabbedPane();
		openFiles = new ArrayList<File>();
		undoManagers = new ArrayList<UndoManager>();
		clipboard = new String();
		latestNewFile = 0;
		try {
			dockIcon = ImageIO.read(new File("resources/img/icon/office.png/"));
		} catch (IOException e) {}
	}
	
	private void createGUI() {
		application.setDockIconImage(dockIcon);
		setSize(1000, 700);
		setLocation(200, 100);
		fileMenu.add(newFileButton);
		fileMenu.add(openFileButton);
		fileMenu.add(saveFileButton);
		fileMenu.add(closeFileButton);
		editMenu.add(undoButton);
		editMenu.add(redoButton);
		editMenu.addSeparator();
		editMenu.add(cutButton);
		editMenu.add(copyButton);
		editMenu.add(pasteButton);
		editMenu.addSeparator();
		editMenu.add(selectAllButton);
		spellCheckMenu.add(runSpellCheckButton);
		spellCheckMenu.add(configureSpellCheckButton);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(spellCheckMenu);
		setJMenuBar(menuBar);
		add(fileTabbedPane, BorderLayout.CENTER);
		try
        {
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("resources/img/icon/cursor.png/").getImage(),new Point(0,0),"custom cursor"));
        }catch(Exception e){}
	}
	
	private void addActions() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create a new file
		newFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				meatTextPane newTextPane = new meatTextPane();
				UndoManager undoManager = new UndoManager();
				undoManagers.add(undoManager);
				openFiles.add(null);
				(newTextPane.getDocument()).addUndoableEditListener(new UndoableEditListener() {
					public void undoableEditHappened(UndoableEditEvent e) {
						undoManager.addEdit(e.getEdit());
						if (undoManager.canUndo()) undoButton.setEnabled(true);
						else undoButton.setEnabled(false);
						if (undoManager.canRedo()) redoButton.setEnabled(true);
						else redoButton.setEnabled(false);
					}
				});
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
			}
		});
		newFileButton.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, (Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()))
				);
		
		//open a file
		openFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser openChooser = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt Files", "txt");
				String name;
				openChooser.setFileFilter(filter);
				if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = openChooser.getSelectedFile();
					name = selectedFile.getName();
					try(BufferedReader br = new BufferedReader(new FileReader(selectedFile))) {
					    StringBuilder sb = new StringBuilder();
					    String line = br.readLine();
					    while (line != null) {
					        sb.append(line);
					        sb.append(System.lineSeparator());
					        line = br.readLine();
					    }
					    String everything = sb.toString();
					    meatTextPane openTextPane = new meatTextPane();
					    openTextPane.setText(everything);
					    UndoManager undoManager = new UndoManager();
					    (openTextPane.getDocument()).addUndoableEditListener(new UndoableEditListener() {
							public void undoableEditHappened(UndoableEditEvent e) {
								undoManager.addEdit(e.getEdit());
								if (undoManager.canUndo()) undoButton.setEnabled(true);
								else undoButton.setEnabled(false);
								if (undoManager.canRedo()) redoButton.setEnabled(true);
								else redoButton.setEnabled(false);
							}
						});
					    JScrollPane scroll = new JScrollPane(openTextPane);
					    scroll.getVerticalScrollBar().setUI(new MeatScrollBarUI());
					    JPanel DocPanel = new JPanel();
						DocPanel.setLayout(new BorderLayout());
						DocPanel.add(scroll, BorderLayout.CENTER);
						DocPanel.setName(name);
					    fileTabbedPane.add(DocPanel);
					    openFiles.add(selectedFile);
					    undoManagers.add(undoManager);
					    fileTabbedPane.setSelectedComponent(DocPanel);
					    openTextPane.requestFocus();
					} catch (FileNotFoundException e1) {
						System.out.println("File Not Found");
					} catch (IOException e1) {
						System.out.println("IO Exception");
						e1.printStackTrace();
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
				
				//override JFileChooser method to display confirmation window on overwrite
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
				if(openFiles.get(fileTabbedPane.getSelectedIndex()) != null){
					saveChooser.setSelectedFile(openFiles.get(fileTabbedPane.getSelectedIndex()));
				}
				
				//save the file if a location is selected
				if (saveChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = saveChooser.getSelectedFile();
					try(FileWriter fw = new FileWriter(selectedFile)) {
						JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
						BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
						JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
						JViewport vp = selectedScrollPane.getViewport();
						meatTextPane selectedTextPane = (meatTextPane) vp.getView();
						fw.write(selectedTextPane.getText());
						openFiles.add(fileTabbedPane.getSelectedIndex(), selectedFile);
						openFiles.remove(fileTabbedPane.getSelectedIndex()+1);
						fileTabbedPane.setTitleAt(fileTabbedPane.getSelectedIndex(), selectedFile.getName());
					} catch (IOException e1) {
						System.out.println("Couldn't Save File");
					}
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
				openFiles.remove(index);
				undoManagers.remove(index);
				fileTabbedPane.remove(index);
			}
		});
		
		//undo most recent change made
		undoButton.addActionListener(new ActionListener() {
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
		
		//copy selection to clipboard and delete selection
		cutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JPanel currentDoc = (JPanel) fileTabbedPane.getSelectedComponent();
				BorderLayout DocLayout = (BorderLayout) currentDoc.getLayout();
				JScrollPane selectedScrollPane = (JScrollPane) DocLayout.getLayoutComponent(BorderLayout.CENTER);
				JViewport vp = selectedScrollPane.getViewport();
				meatTextPane selectedTextPane = (meatTextPane) vp.getView();
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
				meatTextPane selectedTextPane = (meatTextPane) vp.getView();
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
				meatTextPane selectedTextPane = (meatTextPane) vp.getView();
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
				meatTextPane selectedTextPane = (meatTextPane) vp.getView();
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
					meatTextPane currentTextPane = (meatTextPane) vp.getView();
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
					meatTextPane currentTextPane = (meatTextPane) vp.getView();
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
	}
}

class SpellCheckPane extends JPanel {
	
	private static final long serialVersionUID = 23;
	private String RUNPANEL = "The Run Panel Component";
	private String CONFIGUREPANEL = "The Configure Panel Component";
	private JPanel cardPanel;
	private RunPanel runPanel;
	private ConfigurePanel configurePanel;
	private meatButton closeButton;
	private CardLayout cardLayout;
	private File wordListFile, keyboardFile;
	private String defaultWordListPath = "resources/wordlist.wl";
	private String defaultKeyboardFilePath = "resources/qwerty-us.kb";
	private meatTextPane currentTextPane;
	
	SpellCheckPane(meatTextPane textPane, String panel) {
		currentTextPane = textPane;
		instantiateComponents();
		createGUI();
		addActions();
		cardLayout.show(cardPanel, panel);
		cardPanel.revalidate();
		cardPanel.repaint();
	}
	
	public void runSpellCheck(meatTextPane textPane) {
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
		closeButton = new meatButton("Close");
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
	private meatLabel currentWord;
	private meatButton ignoreButton, addButton, changeButton;
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
	private meatTextPane currentTextPane;
	private String text;
	private int offset;
	private int offsetEnd;
	
	RunPanel(meatTextPane textPane, File wlFile, File kbFile) {
		currentTextPane = textPane;
		wordListFile = wlFile;
		keyboardFile = kbFile;
		instantiateComponents();
		createGUI();
		addActions();
		nextError();
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
		currentWord = new meatLabel("No Mispelled Words");
		ignoreButton = new meatButton("Ignore");
		addButton = new meatButton("Add");
		changeButton = new meatButton("Change");
		currentOptions = new JComboBox<String>();
		currentOptions.setUI(new meatComboBoxUI());
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
		this.setBorder(BorderFactory.createTitledBorder("Spell Check"));
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
	private meatLabel wordListName, keyboardName;
	private meatButton selectWordListButton, selectKeyboardButton;
	private String defaultWordListPath = "resources/wordlist.wl";
	private String defaultKeyboardFilePath = "resources/qwerty-us.kb";
	File wordListFile, keyboardFile;
	
	
	ConfigurePanel() {
		instantiateComponents();
		createGUI();
		addActions();
	}
	
	void instantiateComponents() {
		wordListFile = new File(defaultWordListPath);
		keyboardFile = new File(defaultKeyboardFilePath);
		wordListName = new meatLabel(wordListFile.getName());
		keyboardName = new meatLabel(keyboardFile.getName());
		selectWordListButton = new meatButton("Select Word List");
		selectKeyboardButton = new meatButton("Select Kayboard Layout");
	}
	
	void createGUI() {
		this.setBorder(BorderFactory.createTitledBorder("Configure"));
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
				if (openChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					selectedFile = openChooser.getSelectedFile();
					keyboardFile = selectedFile;
					keyboardName.setText(keyboardFile.getName());
				}
			}
		});
	}
}