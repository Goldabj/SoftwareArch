package platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javax.swing.JPanel;

import platform.services.IGuiRegistrationService;

public class GuiRegistrationService extends Observable implements IGuiRegistrationService {
	
	private Map<String, JPanel> guis;
	
	public GuiRegistrationService() {
		this.guis = new HashMap<>();
	}
	
	@Override
	public void registerGui(String className, JPanel panel) {
		this.guis.put(className, panel);
		setChanged();
		notifyObservers();
	}
	
	public Map<String, JPanel> getGuies() {
		return this.guis;
	}
	
	
}
