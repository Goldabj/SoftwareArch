package edu.rosehulman.p2p.impl.filters;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.p2p.listeners.IRequestLogListener;
import edu.rosehulman.p2p.protocol.IFilter;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;

public class RequestLogFilter implements IFilter {
	
	private List<IPacket> requests;
	private List<IRequestLogListener> listeners;
	
	public RequestLogFilter() {
		this.requests = new ArrayList<>();
		this.listeners = new ArrayList<>();
	}

	@Override
	public void filter(IHost remoteHost, IPacket packet) {
		this.logRequest(packet, remoteHost);
	}
	
	public void logRequest(IPacket p, IHost remoteHost) {
		this.requests.add(p);
		this.updateListeners(p, remoteHost);
	}
	
	public List<IPacket> getRequestLog() {
		return this.requests;
	}
	
	public void addListener(IRequestLogListener listener) {
		this.listeners.add(listener);
	}
	
	public void remoteListener(IRequestLogListener listener) {
		this.listeners.remove(listener);
	}
	
	public void updateListeners(IPacket p, IHost remoteHost) {
		for (IRequestLogListener listener : this.listeners) {
			listener.newRequestUpdate(p, remoteHost);
		}
	}

}
