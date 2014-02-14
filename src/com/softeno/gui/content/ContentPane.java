/**
 * 
 */
package com.softeno.gui.content;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

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
		CommandConstants, ChangeListener {

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
		setOpaque(false);
		setBackground(new Color(0, 0, 0, 0));
		initTab();
		new FileDrop(tab, Program.getCurrentProgram().new DropListener());
	}

	@Override
	public void updateUI() {
		super.updateUI();
	}

	private void initTab() {
		tab = new TabView();
		tab.addChangeListener(this);

		setLayer(tab, 1);
		add(tab);
	}

	

	public void saveDocumentAs() {
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
		if (cmd.equals(CM_COLOR)) {
			Color c = JColorChooser.showDialog(this, "selectColor", null);
			if (c != null) {
				textEditor.setForeground(c);
			}
		} else if (cmd.equals(CM_CLOSE_TAB)) {
			int indx = tab.getSelectedIndex();
			if (indx != -1) {
				tab.remove(indx);
			}
		} else if (cmd.equals(CM_VIEW_BYTE)) {
			JCheckBoxMenuItem mit = (JCheckBoxMenuItem) e.getSource();
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

	public TextEditor addEditor(File file) {
		textEditor = Program.getCurrentProgram().createEditor(file);
		file = textEditor.getFile();
		JScrollPane pane = new JScrollPane(textEditor);
		tab.addTab(file.getName(), UIManager.getIcon("FileView.fileIcon"),
				pane, file.getPath());
		int index = tab.indexOfComponent(pane);
		tab.setSelectedIndex(index);
		return textEditor;
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
