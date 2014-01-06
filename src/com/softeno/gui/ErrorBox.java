/**
 * 
 */
package com.softeno.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * @author anders
 *
 */
public class ErrorBox extends JFrame implements ActionListener {

	public ErrorBox() {
		setUndecorated(true);
	    getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
	}
	
	/**
	 * 
	 */	
	public void setErrorText(String s){
		JLabel message = new JLabel(s);
		add(message, BorderLayout.CENTER);
		JButton but = new JButton("ok");
		but.addActionListener(this);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		p.add(but, BorderLayout.CENTER);
		add(but);
		setVisible(true);
		setSize(new Dimension(120,100));
		setTitle("Feilmelding");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
}
