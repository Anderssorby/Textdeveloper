/**
 * 
 */
package com.softeno.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import com.softeno.gui.content.ContentPane;

/**
 * @author anders
 *
 */
public class MainWindow extends JFrame implements CommandConstants,
MouseListener, MouseMotionListener {

	private ContentPane contentPane;
	private Point initialPress;


	/**
	 * 
	 */

	public MainWindow()
	{
		super("Text developer");

		setUndecorated(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setSize(600, 600);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setIconImage(new ImageIcon("icons/accessories_text_editor.png").getImage());
	}

	public void initMainWin() {
		contentPane = new ContentPane();
		setContentPane(contentPane);
		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);
		setVisible(true);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screen = toolkit.getScreenSize();
		setLocation((screen.width-getWidth())/2, (screen.height-getHeight())/2);
	}

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
		if (initialPress != null) {
			Point p = e.getLocationOnScreen();
			p.translate(-initialPress.x, -initialPress.y);
			setLocation(p);
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}
}
