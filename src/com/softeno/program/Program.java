/**
 * 
 */
package com.softeno.program;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.StyledDocument;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.softeno.gui.CommandConstants;
import com.softeno.gui.FileDrop;
import com.softeno.gui.MainWindow;
import com.softeno.gui.RootUI;
import com.softeno.gui.editor.TextEditor;

/**
 * @author anders
 * 
 */
public class Program implements ActionListener, CommandConstants {

	private SaveAction saveAction;
	private NewAction newAction;
	private OpenAction openAction;
	private RedoAction redoAction;
	private UndoAction undoAction;

	public class DropListener implements FileDrop.Listener {
		@Override
		public void filesDropped(File[] files) {
			for (File f : files) {
				getMainWindow().getContentPane().addEditor(f);
			}
		}
	}

	class SaveAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			TextEditor textEditor = getMainWindow().getContentPane()
					.getTextEditor();
			if (textEditor.getFile().exists()) {
				textEditor.saveDocument();
			} else {
				getMainWindow().getContentPane().saveDocumentAs();
			}
		}
	}

	class OpenAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			Program pm = Program.getCurrentProgram();
			MainWindow mainWin = pm.getMainWindow();
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filterTxt = new FileNameExtensionFilter(
					"tekstfiler", "txt");
			FileNameExtensionFilter filterAs = new FileNameExtensionFilter(
					"Action Script source files", "as");
			chooser.addChoosableFileFilter(filterAs);
			chooser.addChoosableFileFilter(filterTxt);
			int returnVal = chooser.showOpenDialog(mainWin);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selected = chooser.getSelectedFile();
				getMainWindow().getContentPane().addEditor(selected);
			}
		}

	}

	class NewAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			getMainWindow().getContentPane().addEditor(null);
		}
	}

	class UndoAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				undoEdit.undo();
			} catch (CannotUndoException r) {

			}
		}
	}

	class RedoAction extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				undoEdit.redo();
			} catch (CannotUndoException r) {

			}
		}
	}

	private static Program currentProgram;

	public static Program getCurrentProgram() {
		return currentProgram;
	}

	private MainWindow mainWindow;
	private StyledDocument currentDocument;
	private TextEditor activeEditor;
	private UndoManager undoEdit;

	private Program() {

	}

	void initProgram() {
		mainWindow = new MainWindow();
		mainWindow.initMainWin();

		undoEdit = new UndoManager();

		initActions();
		initMenu();
		initKeyBindings();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (currentProgram == null) {
			currentProgram = new Program();
			currentProgram.initProgram();
		}
		if (args.length > 0) {
			currentProgram.loadFiles(args);
		} else {
			currentProgram.mainWindow.getContentPane().addEditor(null);
		}
	}

	private void loadFiles(String[] args) {
		for (String s : args) {
			File f = new File(s);
			if (f.exists()) {
				mainWindow.getContentPane().addEditor(f);
			}
		}
	}
	
	public TextEditor createEditor(File file) {
		TextEditor textEditor;
		if (file == null) {
			textEditor = new TextEditor();
			file = textEditor.getFile();
		} else {
			textEditor = new TextEditor(file);
		}
		textEditor.getStyledDocument().addUndoableEditListener(undoEdit);
		textEditor.addMouseListener(getMainWindow().getPopupListener());
		return textEditor;
	}

	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public StyledDocument getCurrentDocument() {
		return currentDocument;
	}

	public TextEditor getActiveEditor() {
		return activeEditor;
	}

	private void initKeyBindings() {
		InputMap inmap = new ComponentInputMap(getMainWindow().getContentPane());
		ActionMap actmap = new ActionMap();
		inmap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
				CM_FILE_SAVE);
		actmap.put(CM_FILE_SAVE, saveAction);

		inmap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
				CM_FILE_OPEN);
		actmap.put(CM_FILE_OPEN, openAction);

		inmap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
				CM_NEW);
		actmap.put(CM_NEW, newAction);

		inmap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK),
				CM_UNDO);
		actmap.put(CM_UNDO, undoAction);
		
		inmap.put(
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK),
				CM_REDO);
		actmap.put(CM_REDO, redoAction);
		getMainWindow().getContentPane().setInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW, inmap);
		getMainWindow().getContentPane().setActionMap(actmap);
	}

	private void initActions() {
		saveAction = new SaveAction();
		saveAction.putValue(Action.NAME, "Save");
		saveAction.putValue(Action.SMALL_ICON,
				UIManager.getIcon("FileView.floppyDriveIcon"));

		openAction = new OpenAction();
		openAction.putValue(Action.NAME, "Open");
		openAction.putValue(Action.SMALL_ICON,
				UIManager.getIcon("FileView.fileIcon"));

		newAction = new NewAction();
		newAction.putValue(Action.NAME, "New");
		newAction.putValue(Action.SMALL_ICON,
				UIManager.getIcon("FileView.fileIcon"));

		undoAction = new UndoAction();
		undoAction.putValue(Action.NAME, "Undo");
		undoAction.putValue(Action.SMALL_ICON,
				UIManager.getIcon("FileView.fileIcon"));

		redoAction = new RedoAction();
		redoAction.putValue(Action.NAME, "Redo");
		redoAction.putValue(Action.SMALL_ICON,
				UIManager.getIcon("FileView.fileIcon"));

	}

	private void initMenu() {
		JPopupMenu menu = getMainWindow().getPopupMenu();

		JMenu mFile = new JMenu("File");
		mFile.add(newAction);
		mFile.add(openAction);
		mFile.add(saveAction);
		menu.add(mFile);

		JMenu edit = new JMenu("Edit");
		edit.add(undoAction);
		edit.add(redoAction);
		edit.addSeparator();
		JCheckBoxMenuItem bvw = new JCheckBoxMenuItem("Byte view");
		bvw.setActionCommand(CM_VIEW_BYTE);
		bvw.addActionListener(this);
		edit.add(bvw);
		menu.add(edit);

		JMenu win = new JMenu("Window");
		JMenuItem clt = new JMenuItem("Lukk fane");
		clt.setActionCommand(CM_CLOSE_TAB);
		clt.addActionListener(this);
		win.add(clt);
		JMenuItem farg = new JMenuItem("Farge");
		farg.setActionCommand(CM_COLOR);
		farg.addActionListener(this);
		win.add(farg);
		win.addSeparator();
		RootUI rootUI = ((RootUI) getMainWindow().getRootPane().getUI());
		win.add(rootUI.getRestoreAction());
		win.add(rootUI.getMaximizeAction());
		win.add(rootUI.getIconifyAction());
		win.add(rootUI.getCloseAction());
		win.addSeparator();
		JMenuItem dokill = new JMenuItem("Avslutt");
		dokill.setActionCommand(CM_EXIT);
		dokill.addActionListener(Program.getCurrentProgram());
		win.add(dokill);
		menu.add(win);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CM_EXIT)) {
			System.exit(0);
		} else if (cmd.equals(CM_MAXIMIZE)) {
			MainWindow mwin = getMainWindow();
			mwin.setExtendedState(mwin.getExtendedState()
					| MainWindow.MAXIMIZED_BOTH);
		} else if (cmd.equals(CM_MINIMIZE)) {
			getMainWindow().setState(Frame.ICONIFIED);
		}
	}

	public static URL findResource(String name) {
		URL url = null;
		try {
			File file = new File(name);
			if (!file.exists()) {
				ClassLoader cl = Program.class.getClassLoader();
				url = cl.getResource(name);
			} else {
				url = file.toURI().toURL();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}

}
