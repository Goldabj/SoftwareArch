package edu.rosehulman.helloosgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import edu.rosehulman.GreetingService.GreetingService;
import edu.rosehulman.workingdirectoryservice.WorkingDirectoryService;

public class Activator implements BundleActivator {
	private ServiceReference greetingServiceReference;
	private ServiceReference wdServiceReference;

	public void start(BundleContext context) throws Exception {
		System.out.println("Hello World!!");
		greetingServiceReference = context.getServiceReference(GreetingService.class.getName());
		GreetingService greetingService = (GreetingService) context.getService(greetingServiceReference);
		System.out.println(greetingService.sayHello());
		
		wdServiceReference = context.getServiceReference(WorkingDirectoryService.class.getName());
		WorkingDirectoryService wdService = (WorkingDirectoryService) context.getService(wdServiceReference);
		wdService.printWorkingDirectory();
	}

	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye World!!");
		context.ungetService(greetingServiceReference);
		context.ungetService(wdServiceReference);
	}
}
