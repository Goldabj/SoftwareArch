package platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import platform.services.IGuiRegistrationService;
import platform.services.IStatusService;

public class Platform extends Observable implements Observer{
	
	private static final Platform instance = new Platform();
	private GuiRegistrationService guiRegService;
	private StatusService statusService;
	
	private Platform() {
		this.guiRegService = new GuiRegistrationService();
		this.statusService = new StatusService();
		this.guiRegService.addObserver(this);
		this.statusService.addObserver(this);
	}
	
	public static Platform getInstance() {
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public List<ServiceRegistration> registerServices(BundleContext context) {
		List<ServiceRegistration> regs = new ArrayList<>();
		ServiceRegistration reg1 = context.registerService(IGuiRegistrationService.class.getName(),
				this.guiRegService, null);
		regs.add(reg1);
		
		ServiceRegistration reg2 = context.registerService(IStatusService.class.getName(),
				this.statusService, null);
		regs.add(reg2);
		
		return regs;
	}
	
	public Map<String, JPanel> getGuis() {
		return this.guiRegService.getGuies();
	}
	
	public List<String> getStatuses() {
		return this.statusService.getPosts();
	}

	@Override
	public void update(Observable o, Object arg) {
		setChanged();
		notifyObservers(arg);
		clearChanged();
	}	
	
}
