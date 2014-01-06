/**
 * 
 */
package com.softeno.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import com.softeno.gui.content.ContentPane;
import com.softeno.gui.editor.TextEditor;

/**
 * @author anders
 *
 */
public class MainWindow extends JFrame implements CommandConstants {
	
	private ContentPane contentPane;

	/**
	 * 
	 */
	
	public MainWindow()
	{
		super("Text developer");
		
//	    setUndecorated(true);
//	    getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
	    
	    setSize(600, 600);
	    setDefaultCloseOperation(EXIT_ON_CLOSE);
	    
		setIconImage(new ImageIcon("icons/accessories_text_editor.png").getImage());
	}
	
	public void initMainWin() {
		contentPane = new ContentPane();
		setContentPane(contentPane);
		
		JMenu mFile = new JMenu("Fil");
		JMenuItem ny = new JMenuItem("Ny", UIManager.getIcon("FileView.fileIcon"));
		ny.setActionCommand(CM_NEW);
		ny.addActionListener(contentPane);
		mFile.add(ny);
		JMenuItem open = new JMenuItem("Åpne");
		open.setActionCommand(CM_FILE_OPEN);
		open.addActionListener(contentPane);
		mFile.add(open);
		JMenuItem save = new JMenuItem("Lagre", UIManager.getIcon("FileView.floppyDriveIcon"));
		save.setMnemonic(KeyEvent.VK_S);
		save.setActionCommand(CM_FILE_SAVE);
		save.addActionListener(contentPane);
		mFile.add(save);
		JSeparator separator = new JSeparator();
		mFile.add(separator);
		JMenuItem dokill = new JMenuItem("Avslutt");
		dokill.setActionCommand(CM_EXIT);
		mFile.add(dokill);
		JMenuBar topMenu = new JMenuBar();
		topMenu.add(mFile);
		
		JMenu edit = new JMenu("Rediger");
		JMenuItem undo = new JMenuItem("Angre");
		undo.setActionCommand(CM_UNDO);
		undo.addActionListener(contentPane);
		edit.add(undo);
		JMenuItem redo = new JMenuItem("Gjør om");
		redo.setActionCommand(CM_REDO);
		redo.addActionListener(contentPane);
		edit.add(redo);
		separator = new JSeparator();
		edit.add(separator);
		JCheckBoxMenuItem bvw = new JCheckBoxMenuItem("Byte view");
		bvw.setActionCommand(CM_VIEW_BYTE);
		bvw.addActionListener(contentPane);
		edit.add(bvw);
		topMenu.add(edit);
		
		JMenu win = new JMenu("Window");
		JMenuItem clt = new JMenuItem("Lukk fane");
		clt.setActionCommand(CM_CLOSE_TAB);
		clt.addActionListener(contentPane);
		win.add(clt);
		JMenuItem farg = new JMenuItem("Farge");
		farg.setActionCommand(CM_COLOR);
		farg.addActionListener(contentPane);
		win.add(farg);
		topMenu.add(win);
		
		setJMenuBar(topMenu);
				
	    setVisible(true);

	    Toolkit toolkit = Toolkit.getDefaultToolkit();
	    Dimension screen = toolkit.getScreenSize();
	    setLocation((screen.width-getWidth())/2, (screen.height-getHeight())/2);
	}
	
	public ContentPane getContentPane() {
		return contentPane;
	}
}
