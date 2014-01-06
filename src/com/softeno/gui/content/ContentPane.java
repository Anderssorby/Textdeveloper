/**
 * 
 */
package com.softeno.gui.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
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

	public TextEditor getTextEditor() {
		return textEditor;
	}

	/**
	 * 
	 */

	public ContentPane() {
		setLayout(new BorderLayout());
		addMouseListener(this);
		new FileDrop( this, new FileDrop.Listener()
	      {   public void filesDropped( java.io.File[] files )
	          {   
	              for (File f : files) {
	            	  createEditor(f);
	              }
	          }   
	      }); 
		
		tab = new JTabbedPane();
		tab.addChangeListener(this);
		add(tab, BorderLayout.CENTER);

		undoEdit = new UndoManager();

		JButton col = new JButton("Farge");
		col.setActionCommand(CM_COLOR);
		col.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd.equals(CM_FILE_SAVE)) {
			JFileChooser chooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("text files", "txt");
			chooser.addChoosableFileFilter(filter);
			Program pm = Program.getCurrentProgram();
			MainWindow mainWin = pm.getMainWindow();
			int returnVal = chooser.showSaveDialog(mainWin);
			if (returnVal == JFileChooser.APPROVE_OPTION)
			{
				File selected = chooser.getSelectedFile();
				if (textEditor.setFile(selected)) {
					textEditor.saveDocument();
					Component com = tab.getSelectedComponent();
					int idx = tab.getSelectedIndex();
					tab.remove(com);
					tab.insertTab(selected.getName(), UIManager.getIcon("FileView.fileIcon"), com, selected.getPath(), idx);
					repaint();
				}
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
	
	public TextEditor createEditor(File file) {
		if (file == null) {
			textEditor = new TextEditor();
			JScrollPane pane = new JScrollPane(textEditor);
			textEditor.getStyledDocument().addUndoableEditListener(undoEdit);
			tab.addTab("Untitled", UIManager.getIcon("FileView.fileIcon"), pane, "Untitled");
			int index = tab.indexOfComponent(pane);
			tab.setSelectedIndex(index);
		} else {
			textEditor = new TextEditor(file);
			textEditor.getStyledDocument().addUndoableEditListener(undoEdit);
			JScrollPane pane = new JScrollPane(textEditor);
			tab.addTab(file.getName(), UIManager.getIcon("FileView.fileIcon"), pane, file.getPath());
			int index = tab.indexOfComponent(pane);
//			JPanel panel = new JPanel();
//			JLabel label = new JLabel(tab.getTitleAt(index), tab.getIconAt(index), JLabel.LEADING);
//			panel.add(label);
//			panel.add(new JButton(new ImageIcon("close.png")));
//			tab.setTabComponentAt(index, panel);
			tab.setSelectedIndex(index);
		}
		return textEditor;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

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
