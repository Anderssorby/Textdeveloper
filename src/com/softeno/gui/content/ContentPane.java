/**
 * 
 */
package com.softeno.gui.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
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
public class ContentPane extends JPanel implements ActionListener,
CommandConstants, MouseInputListener, ChangeListener {

	private UndoManager undoEdit;
	private TextEditor textEditor;
	private JTabbedPane tab;
	private JPopupMenu menu;
	private PopupListener popupListener;

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

	public TextEditor getTextEditor() {
		return textEditor;
	}

	/**
	 * 
	 */

	 public ContentPane() {
		setLayout(new BorderLayout());
		
		new FileDrop( this, new FileDrop.Listener()
		{   public void filesDropped( java.io.File[] files )
		{   
			for (File f : files) {
				createEditor(f);
			}
		}   
		}); 

		initMenu();
		initTab();
		addMouseListener(this);
	 }
	 
	 private void initTab() {
		 tab = new JTabbedPane();
		 tab.addChangeListener(this);
		 tab.addMouseListener(popupListener);
		 add(tab, BorderLayout.CENTER);

		 undoEdit = new UndoManager();
	 }

	 private void initMenu() {
		 menu = new JPopupMenu();
		 popupListener = new PopupListener();

		 JMenu mFile = new JMenu("Fil");
		 JMenuItem ny = new JMenuItem("Ny", UIManager.getIcon("FileView.fileIcon"));
		 ny.setActionCommand(CM_NEW);
		 ny.addActionListener(this);
		 mFile.add(ny);
		 JMenuItem open = new JMenuItem("�pne");
		 open.setActionCommand(CM_FILE_OPEN);
		 open.addActionListener(this);
		 mFile.add(open);
		 JMenuItem save = new JMenuItem("Lagre", UIManager.getIcon("FileView.floppyDriveIcon"));
		 save.setMnemonic(KeyEvent.VK_S);
		 save.setActionCommand(CM_FILE_SAVE);
		 save.addActionListener(this);
		 mFile.add(save);
		 mFile.addSeparator();
		 JMenuItem dokill = new JMenuItem("Avslutt");
		 dokill.setActionCommand(CM_EXIT);
		 mFile.add(dokill);
		 menu.add(mFile);

		 JMenu edit = new JMenu("Rediger");
		 JMenuItem undo = new JMenuItem("Angre");
		 undo.setActionCommand(CM_UNDO);
		 undo.addActionListener(this);
		 edit.add(undo);
		 JMenuItem redo = new JMenuItem("Gj�r om");
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
			 if (textEditor.getFile().exists()) {
				 textEditor.saveDocument();
			 } else {
				saveDocumentAs();
			 }
		 } else if (cmd.equals(CM_FILE_OPEN)) {
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
		 } else if (cmd.equals(CM_NEW)) {
			 createEditor(null);
			 repaint();
		 } else if (cmd.equals(CM_EXIT)) {
			 System.exit(0);
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
		 // TODO Auto-generated method stub

	 }

	 @Override
	 public void mouseExited(MouseEvent e) {
		 // TODO Auto-generated method stub

	 }

	 @Override
	 public void mouseDragged(MouseEvent e) {
		 // TODO Auto-generated method stub

	 }

	 @Override
	 public void mouseMoved(MouseEvent e) {
		 // TODO Auto-generated method stub

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
