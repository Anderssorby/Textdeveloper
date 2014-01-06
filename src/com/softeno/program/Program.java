/**
 * 
 */
package com.softeno.program;

import java.io.File;

import javax.swing.text.StyledDocument;

import com.softeno.gui.MainWindow;
import com.softeno.gui.editor.TextEditor;

/**
 * @author anders
 *
 */
public class Program {
	
	private static Program currentProgram;
	
	public static Program getCurrentProgram() {
		return currentProgram;
	}

	private MainWindow mainWindow;
	private StyledDocument currentDocument;
	private TextEditor activeEditor;
	
	private Program() {
		
	}
	
	void initProgram() {
		mainWindow = new MainWindow();
		mainWindow.initMainWin();
		
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
			currentProgram.mainWindow.getContentPane().createEditor(null);
		}
	}

	private void loadFiles(String[] args) {
		for (String s : args) {
			File f = new File(s);
			if (f.exists()) {
				mainWindow.getContentPane().createEditor(f);
			}
		}
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

}