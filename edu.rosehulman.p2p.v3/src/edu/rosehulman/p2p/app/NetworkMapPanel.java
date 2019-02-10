package edu.rosehulman.p2p.app;



import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class NetworkMapPanel extends JPanel{
	private static final long serialVersionUID = 9000924958885974225L;

	public NetworkMapPanel() {
		super(new BorderLayout());
		this.setBorder(BorderFactory.createTitledBorder("Network Graph"));
		
		this.add(new JLabel("Shown the network graph (Bonus) ..."));
		
	}

}
