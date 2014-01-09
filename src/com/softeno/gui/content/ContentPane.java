/**
 * 
 */
package com.softeno.gui.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import com.softeno.gui.CommandConstants;
import com.softeno.gui.FileDrop;
import com.softeno.gui.MainWindow;
import com.softeno.gui.editor.TextEditor;
import com.softeno.program.Program;

/**
 * @author anders
 * 
 */
public class ContentPane extends JLayeredPane implements ActionListener,
CommandConstants, MouseInputListener, ChangeListener {

	private UndoManager undoEdit;
	private TextEditor textEditor;
	private JTabbedPane tab;
	private JPopupMenu menu;
	private PopupListener popupListener;
	private JPanel windowControls;
	private JLabel statusText;

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				menu.show(e.getComponent(),
						e.getX(), e.getY());
			}
		}
	}

	class DropListener implements FileDrop.Listener
	{  
		public void filesDropped(File[] files )
		{   
			for (File f : files) {
				createEditor(f);
			}
		}   
	}

	public TextEditor getTextEditor() {
		return textEditor;
	}

	/**
	 * 
	 */

	public ContentPane() {
		setLayout(new BorderLayout());
		Border b = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.GRAY, Color.DARK_GRAY);
		setBorder(b);
		setOpaque(false);
		setBackground(new Color( 0, 0, 0 ,0));
		initWindowControls();
		initMenu();
		initTab();
		initStatusText();
		initKeyBindings();
		new FileDrop( tab, new DropListener()); 
		addMouseListener(this);
	}

	private void initWindowControls() {
		windowControls = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));

		ImageIcon icon = new ImageIcon("icons/minimize.png");
		icon.setImage(icon.getImage().getScaledInstance(18, 18, Image.SCALE_DEFAULT));
		JButton minim = new JButton(icon);
		minim.setToolTipText("minimize");
		minim.setPreferredSize(new Dimension(20, 20));
		minim.setActionCommand(CM_MINIMIZE);
		minim.addActionListener(Program.getCurrentProgram());
		windowControls.add(minim);

		icon = new ImageIcon("icons/maximize.png");
		icon.setImage(icon.getImage().getScaledInstance(18, 18, Image.SCALE_DEFAULT));
		JButton maxim = new JButton(icon);
		maxim.setToolTipText("maximize");
		maxim.setPreferredSize(new Dimension(20, 20));
		maxim.setActionCommand(CM_MAXIMIZE);
		maxim.addActionListener(Program.getCurrentProgram());
		windowControls.add(maxim);

		icon = new ImageIcon("icons/close.png");
		icon.setImage(icon.getImage().getScaledInstance(18, 18, Image.SCALE_DEFAULT));
		JButton close = new JButton(icon);
		close.setToolTipText("close");
		close.setPreferredSize(new Dimension(20, 20));
		close.setActionCommand(CM_EXIT);
		close.addActionListener(Program.getCurrentProgram());
		windowControls.add(close);

		windowControls.setSize(new Dimension(60, 20));
		int width = Program.getCurrentProgram().getMainWindow().getWidth();
		windowControls.setLocation(width-windowControls.getWidth()-3, 3);
		setLayer(windowControls, 10);
		add(windowControls);
	}

	public void updateUI() {
		super.updateUI();
	}

	private void initTab() {
		tab = new TabView();
		tab.setOpaque(false);
		tab.setBackground(new Color( 0, 0, 0 ,0));
		tab.addChangeListener(this);
		tab.addMouseListener(popupListener);

		setLayer(tab, 1);
		add(tab);

		undoEdit = new UndoManager();
	}
	
	private void initKeyBindings() {
		Action save = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		};
		tab.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK),
				CM_FILE_SAVE);
		tab.getActionMap().put(CM_FILE_SAVE, save);
		
		Action open = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		};
		tab.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK),
				CM_FILE_OPEN);
		tab.getActionMap().put(CM_FILE_OPEN, open);
		
		Action neww = new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				createEditor(null);
				repaint();
			}
		};
		tab.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK),
				CM_NEW);
		tab.getActionMap().put(CM_NEW, neww);
	}

	private void initStatusText() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0));
		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		int height = Program.getCurrentProgram().getMainWindow().getHeight();
		statusText = new JLabel("");
		statusText.setHorizontalAlignment(SwingConstants.LEFT);
		statusText.setPreferredSize(new Dimension(100, 20));
		panel.add(statusText);
		panel.setLocation(0, height-20);
		panel.setSize(new Dimension(100, 20));
		setLayer(panel, 9);
		//		add(panel);
	}

	private void initMenu() {
		menu = new JPopupMenu();
		popupListener = new PopupListener();


		JMenu mFile = new JMenu("Fil");
		JMenuItem ny = new JMenuItem("Ny", UIManager.getIcon("FileView.fileIcon"));
		ny.setActionCommand(CM_NEW);
		ny.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.VK_CONTROL));
		ny.addActionListener(this);
		mFile.add(ny);
		JMenuItem open = new JMenuItem("Åpne");
		open.setActionCommand(CM_FILE_OPEN);
		open.addActionListener(this);
		open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.VK_CONTROL));
		mFile.add(open);
		JMenuItem save1 = new JMenuItem("Lagre", UIManager.getIcon("FileView.floppyDriveIcon"));
		save1.setMnemonic(KeyEvent.VK_S);
		save1.setActionCommand(CM_FILE_SAVE);
		save1.addActionListener(this);
		mFile.add(save1);
		menu.add(mFile);

		JMenu edit = new JMenu("Rediger");
		JMenuItem undo = new JMenuItem("Angre");
		undo.setActionCommand(CM_UNDO);
		undo.addActionListener(this);
		edit.add(undo);
		JMenuItem redo = new JMenuItem("Gjør om");
		redo.setActionCommand(CM_REDO);
		redo.addActionListener(this);
		edit.add(redo);
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
		JMenuItem dokill = new JMenuItem("Avslutt");
		dokill.setActionCommand(CM_EXIT);
		dokill.addActionListener(Program.getCurrentProgram());
		win.add(dokill);
		menu.add(win);

	}

	private void saveDocumentAs() {
		JFileChooser chooser = new JFileChooser(textEditor.getFile());
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"text files", "txt");
		chooser.addChoosableFileFilter(filter);
		Program pm = Program.getCurrentProgram();
		MainWindow mainWin = pm.getMainWindow();
		int returnVal = chooser.showSaveDialog(mainWin);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File selected = chooser.getSelectedFile();
			if (textEditor.setFile(selected)) {
				textEditor.saveDocument();
				int idx = tab.getSelectedIndex();
				tab.setTitleAt(idx, selected.getName());
				tab.setIconAt(idx, UIManager.getIcon("FileView.fileIcon"));
				tab.setToolTipTextAt(idx, selected.getPath());
				repaint();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CM_FILE_SAVE)) {
			save();
		} else if (cmd.equals(CM_FILE_OPEN)) {
			open();
		} else if (cmd.equals(CM_NEW)) {
			createEditor(null);
			repaint();
		} else if (cmd.equals(CM_COLOR)) {
			Color c = JColorChooser.showDialog(this, "selectColor", null);
			if (c != null) {
				textEditor.setForeground(c);
			}
		} else if (cmd.equals(CM_UNDO)) {
			try {
				undoEdit.undo();
			} catch (CannotUndoException r) {

			}
		} else if (cmd.equals(CM_REDO)) {
			try {
				undoEdit.redo();
			} catch (CannotUndoException r) {

			}
		} else if (cmd.equals(CM_CLOSE_TAB)) {
			int indx = tab.getSelectedIndex();
			if (indx != -1) {
				tab.remove(indx);
			}
		} else if (cmd.equals(CM_VIEW_BYTE)) {
			JCheckBoxMenuItem mit = (JCheckBoxMenuItem)e.getSource();
			textEditor.enableByteView(mit.isSelected());
		}
	}

	private void save() {
		if (textEditor.getFile().exists()) {
			textEditor.saveDocument();
		} else {
			saveDocumentAs();
		}
	}

	private void open() {
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
			createEditor(selected);
			repaint();
		}
	}

	@Override
	public void addMouseListener(MouseListener l) {
		tab.addMouseListener(l);
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener l) {
		tab.addMouseMotionListener(l);
	}

	public TextEditor createEditor(File file) {
		if (file == null) {
			textEditor = new TextEditor();
			file = textEditor.getFile();
		} else {
			textEditor = new TextEditor(file);
		}
		textEditor.getStyledDocument().addUndoableEditListener(undoEdit);
		textEditor.addMouseListener(popupListener);
		JScrollPane pane = new JScrollPane(textEditor);
		tab.addTab(file.getName(), UIManager.getIcon("FileView.fileIcon"), pane, file.getPath());
		int index = tab.indexOfComponent(pane);
		tab.setSelectedIndex(index);
		return textEditor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point p = e.getPoint();
		if (e.getButton() == MouseEvent.BUTTON3) {
			menu.show(this, p.x, p.y);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if (src.equals(tab)) {
			JTabbedPane pane = (JTabbedPane) src;
			JScrollPane sp = (JScrollPane) pane.getSelectedComponent();
			if (sp != null) {
				textEditor = (TextEditor) sp.getViewport().getComponent(0);
			} else {
				textEditor = null;
			}
		}
	}
}
