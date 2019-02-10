package controller;


import javax.swing.JButton;
import javax.swing.JFrame;

public class GuiRunner {

	public void run() {
		View view = new View();
		ViewController controller = new ViewController(view);
				
		view.show();
	}
}
