package edu.rosehulman.GreetingService.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import edu.rosehulman.GreetingService.GreetingService;

public class GreetingActivator implements BundleActivator {

	private ServiceRegistration greetingServiceRegistration;

	@Override
	public void start(BundleContext context) throws Exception {
		GreetingService greetingService = new GreetingServiceImpl();
		greetingServiceRegistration = context.registerService(GreetingService.class.getName(), greetingService, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		greetingServiceRegistration.unregister();
	}
}
