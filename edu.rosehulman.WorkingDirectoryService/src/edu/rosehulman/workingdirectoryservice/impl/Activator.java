package edu.rosehulman.workingdirectoryservice.impl;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import edu.rosehulman.workingdirectoryservice.WorkingDirectoryService;

public class Activator implements BundleActivator {
	
	private ServiceRegistration reg;

	@Override
	public void start(BundleContext context) throws Exception {
		WorkingDirectoryService wdService = new WorkingDirectoryServiceImpl();
		reg = context.registerService(WorkingDirectoryService.class.getName(), wdService, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		reg.unregister();
	}


}
