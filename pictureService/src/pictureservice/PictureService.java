package pictureservice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;

import services.IPictureService;

public class PictureService implements IPictureService {
	
	JPanel panel;
	JLabel label;
	
	public PictureService() {
		this.panel = new JPanel();
		this.panel.setBackground(Color.magenta);
		this.panel.setPreferredSize(new Dimension(100, 100));
		this.panel.setMaximumSize(new Dimension(100, 100));
		this.panel.setSize(100, 100);
		this.label = new JLabel("ON");
		this.label.setSize(new Dimension(40, 40));
		this.panel.add(label);
	}

	@Override
	public void setText(String text) {
		this.label.setText(text);
	}
	
	@Override
	public String getText() {
		return this.label.getText();
	}

}
