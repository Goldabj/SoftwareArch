package guipuginframework;

import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import controller.GuiRunner;
import platform.Platform;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private Platform platform;
	private List<ServiceRegistration> registrations;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		platform = Platform.getInstance();
		this.registrations = platform.registerServices(context);
		
		GuiRunner runner = new GuiRunner();
		runner.run();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		for (ServiceRegistration reg : this.registrations) {
			reg.unregister();
		}
	}

}
