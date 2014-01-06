/**
 * 
 */
package com.softeno.gui.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;

import com.softeno.gui.ErrorBox;

/**
 * @author anders
 *
 */
public class TextEditor extends JTextPane implements CaretListener {

	private boolean byteView;
	private File file;

	public TextEditor() {
		this(new File(""));
	}


	public TextEditor(File file) {
		super();
		EditorStyledDocument edoc = new EditorStyledDocument();
		setDocument(edoc);
		addCaretListener(this);
		this.setFile(file);
		if (file.exists()) {
			openDocument();
		}
	}


	public void openDocument(){

		try {
			FileInputStream fr = new FileInputStream(file);
			StringBuilder builder = new StringBuilder();
			byte[] textpart = new byte[1000];
			int count = fr.read(textpart);
			String s = new String(textpart);
			if (count < 1000)
				builder.append(s.substring(0, count));
			else {
				builder.append(s);
				while (count == 1000)
				{
					textpart = new byte[1000];
					count = fr.read(textpart);
					s = new String(textpart);
					if (count < 1000){
						builder.append(s.substring(0, count));
						break;
					} else
						builder.append(s);
				}
			}
			fr.close();
			String finalincome = builder.toString();
			setDocument(new EditorStyledDocument(finalincome));
		} catch (IOException e) {
			String string = new String(e.getMessage());
			ErrorBox ebox = new ErrorBox();
			ebox.setErrorText(string);
		}

	}

	public void saveDocument(){
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(this.getText());
			fw.flush();
			fw.close();
		} catch (IOException e) {
			String string = new String(e.getMessage());
			ErrorBox ebox = new ErrorBox();
			ebox.setErrorText(string);
		}

	}


	@Override
	public void caretUpdate(CaretEvent e) {

	}

	public class EditorDocumentListener implements DocumentListener, UndoableEditListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void undoableEditHappened(UndoableEditEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public class EditorStyledDocument extends DefaultStyledDocument {

		public EditorStyledDocument() {
			this("");
		}

		public EditorStyledDocument(String text) {
			super(StyleContext.getDefaultStyleContext());
			EditorDocumentListener editorDocumentListener = new EditorDocumentListener();
			addDocumentListener(editorDocumentListener);
			addUndoableEditListener(editorDocumentListener);
			try {
				this.replace(0, this.getLength(), text, new SimpleAttributeSet());
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

	}

	public void enableByteView(boolean flag) {
		if (flag == byteView)
			return;
		this.byteView = flag;
		if (byteView) {
			String s = String.format("%040x", new BigInteger(1, getText().getBytes()));
			EditorStyledDocument doc = (EditorStyledDocument) getDocument();
			try {
				doc.replace(0, doc.getLength(), s, new SimpleAttributeSet());
				setEditable(false);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else {
			String s = Byte.valueOf(Integer.decode(getText()).byteValue()).toString();
			EditorStyledDocument doc = (EditorStyledDocument) getDocument();
			try {
				doc.replace(0, doc.getLength(), s, new SimpleAttributeSet());
				setEditable(true);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}


	public File getFile() {
		return file;
	}


	public boolean setFile(File file) {
		if (file == null || !file.equals(this.file)) {
			this.file = file;
			return true;
		}
		return false;
	}
}





