/**
 * 
 */
package com.softeno.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
		removeAll();
		setLayout(new FlowLayout());
		JTextArea message = new JTextArea(s);
		add(message, BorderLayout.CENTER);
		JButton but = new JButton("ok");
		but.addActionListener(this);
		add(but);
		setVisible(true);
		pack();
		setTitle("Feilmelding");
	}
	
	public void actionPerformed(ActionEvent arg0) {
		dispose();
	}
}
