package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class View {
	
	private JFrame frame;
	private JPanel executionEnv;
	private JTextArea statusBar;
	private JList<String> pluginList;
	
	public View() {
		this.frame = new JFrame();
		this.executionEnv = new JPanel();
		this.statusBar = new JTextArea();
		this.pluginList = new JList<String>();
		
		this.frame.setLayout(new BorderLayout());
		
		this.statusBar.setPreferredSize(new Dimension(600, 100));
		this.pluginList.setPreferredSize(new Dimension(200, 400));
		this.executionEnv.setPreferredSize(new Dimension(400, 400));
		
		this.executionEnv.setLayout(new BorderLayout());
		
		this.pluginList.setBackground(Color.CYAN);
		this.executionEnv.setBackground(Color.darkGray);
		this.statusBar.setBackground(Color.WHITE);
		
		this.statusBar.setText("Running Framework....\n");
		
		this.frame.add(executionEnv, BorderLayout.EAST);
		this.frame.add(statusBar, BorderLayout.SOUTH);
		this.frame.add(pluginList, BorderLayout.WEST);
		
		this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.frame.setPreferredSize(new Dimension(600, 500));
		this.frame.pack();
	}

	public JFrame getFrame() {
		return frame;
	}

	public JPanel getExecutionEnv() {
		return executionEnv;
	}

	public JTextArea getStatusBar() {
		return statusBar;
	}

	public JList<String> getPluginList() {
		return pluginList;
	}
	
	public void show() {
		this.frame.setVisible(true);
	}
	


}
