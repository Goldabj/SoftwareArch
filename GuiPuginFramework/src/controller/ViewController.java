package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.ListModel;

import javafx.scene.input.MouseButton;
import platform.Platform;
import platform.StatusService;

public class ViewController implements Observer {

	private View view;
	private Platform platform;
	private Map<String, JPanel> runningGuis;

	public ViewController(View view) {
		this.platform = Platform.getInstance();
		this.platform.addObserver(this);
		this.view = view;
		this.runningGuis = new HashMap<>();

		addControllers();
	}

	private void addControllers() {
		MouseListener listListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
//					String selected = view.getPluginList().getSelectedValue();
//					JPanel removed = runningGuis.remove(selected);
//					if (removed != null) {
//						view.getExecutionEnv().remove(removed);
//					}
				} else {

					String selected = view.getPluginList().getSelectedValue();

					JPanel gui = platform.getGuis().get(selected);
					if (gui != null && runningGuis.get(selected) == null) {
						view.getExecutionEnv().add(gui);
						gui.setVisible(true);
						int x = (int) ((Math.random() * 200) + 20);
						int y = (int) ((Math.random() * 200) + 20);
						gui.setLocation(x, y);
						runningGuis.put(selected, gui);
					}
				}
			}
		};
		this.view.getPluginList().addMouseListener(listListener);
	}

	public void updatePluginList() {
		Set<String> name = this.platform.getGuis().keySet();
		DefaultListModel<String> model = new DefaultListModel<>();
		for (String str : name) {
			model.addElement(str);
		}
		this.view.getPluginList().setModel(model);
	}

	public void postStatus(String status) {
		this.view.getStatusBar().append(status + "\n");
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg != null) {
			postStatus((String) arg);
		} else {
			updatePluginList();
		}

	}

}
