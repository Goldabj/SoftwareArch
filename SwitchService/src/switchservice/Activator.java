package switchservice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import platform.services.IGuiRegistrationService;
import platform.services.IStatusService;
import services.IPictureService;

public class Activator implements BundleActivator {

	private static BundleContext context;
	private JPanel panel; 

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		this.context = bundleContext;
		ServiceReference pictureSerRef = context.getServiceReference(IPictureService.class.getName());
		final IPictureService picService = (IPictureService) context.getService(pictureSerRef);
		
		
		this.panel = new JPanel();
		this.panel.setSize(new Dimension(100, 40));
		this.panel.setPreferredSize(new Dimension(100, 40));
		this.panel.setMaximumSize(new Dimension(100, 40));
		JButton button = new JButton("SWITCH");
		button.setSize(new Dimension(100, 40));
		button.setMaximumSize(new Dimension(100, 40));
		button.setBackground(Color.GREEN);
		this.panel.add(button);
		
		button.addActionListener(new ActionListener()
		{
			  public void actionPerformed(ActionEvent e)
			  {
				  if (picService.getText().equals("ON")) {
					  picService.setText("OFF");
				  }else {
					  picService.setText("ON");
				  }

			  }
			});
		
		ServiceReference guiRegServiceRef = context.getServiceReference(IGuiRegistrationService.class.getName());
		IGuiRegistrationService regService = (IGuiRegistrationService) context.getService(guiRegServiceRef);
		regService.registerGui("switchPlugin", this.panel);
		
		ServiceReference messageRef = context.getServiceReference(IStatusService.class.getName());
		IStatusService statusService = (IStatusService) context.getService(messageRef);
		statusService.postStatus("installed switch service");
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
