package pictureservice;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import platform.services.IGuiRegistrationService;
import platform.services.IStatusService;
import services.IPictureService;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		PictureService service = new PictureService();
		System.out.println("running picture service");
		
		ServiceReference guiRegServiceRef = context.getServiceReference(IGuiRegistrationService.class.getName());
		IGuiRegistrationService regService = (IGuiRegistrationService) context.getService(guiRegServiceRef);
		regService.registerGui("picturePlugin", service.panel);
		
		ServiceReference messageRef = context.getServiceReference(IStatusService.class.getName());
		IStatusService statusService = (IStatusService) context.getService(messageRef);
		statusService.postStatus("Installed picture servce");
		
		ServiceRegistration serviceReg = context.registerService(IPictureService.class.getName(), service, null);
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		
	}

}
