package com.softeno.gui.content;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTabbedPane;

import com.softeno.gui.CommandConstants;

public class TabView extends JTabbedPane implements CommandConstants {
	

	public TabView() {
		initConfig();
	}

	public TabView(int tabPlacement) {
		super(tabPlacement);
		initConfig();
	}

	public TabView(int tabPlacement, int tabLayoutPolicy) {
		super(tabPlacement, tabLayoutPolicy);
		initConfig();
	}
	
	private void initConfig() {
		
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		paintComponent(g2);
		paintComponents(g2);
	}

}
