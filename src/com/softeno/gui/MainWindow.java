/**
 * 
 */
package com.softeno.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPopupMenu;

import com.softeno.gui.content.ContentPane;
import com.softeno.program.Program;

/**
 * @author anders
 *
 */
public class MainWindow extends JFrame implements CommandConstants,
MouseListener, MouseMotionListener {
	
	class PopupListener extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private ContentPane contentPane;
	private Point initialPress;
	private JPopupMenu menu;
	private PopupListener popupListener;
	


	/**
	 * 
	 */

	public MainWindow()
	{
		super("Text developer");
		setUndecorated(true);
		getRootPane().setUI(new RootUI());
		
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setIconImage(new ImageIcon(Program.findResource("icons/accessories_text_editor.png")).getImage());
	}

	public void initMainWin() {
		contentPane = new ContentPane();
		setContentPane(contentPane);
		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);
		setVisible(true);

		popupListener = new PopupListener();
		menu = new JPopupMenu();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		setLocation((screen.width-getWidth())/2, (screen.height-getHeight())/2);
	}

	@Override
	public ContentPane getContentPane() {
		return contentPane;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Rectangle handle = new Rectangle(0, 0, getWidth(), 20);
		Point p = e.getPoint();
		if (handle.contains(p)) {
			initialPress = e.getPoint();
		} else {
			initialPress = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (initialPress != null) {
			Point p = e.getLocationOnScreen();
			p.translate(-initialPress.x, -initialPress.y);
			setLocation(p);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}
	
	@Override
	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, y, w, h);
		Window windowControls = ((RootUI) getRootPane().getUI()).getWindowControls();
		Dimension size = windowControls.getSize();
		int space = 6;
		windowControls.setBounds(x + w - size.width, y + space, size.width, size.height);
	}

	public PopupListener getPopupListener() {
		return popupListener;
	}

	public JPopupMenu getPopupMenu() {
		return menu;
	}
}
