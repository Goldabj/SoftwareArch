package edu.rosehulman.p2p.impl.filters;

import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.p2p.impl.Request;
import edu.rosehulman.p2p.listeners.ISendLogListener;
import edu.rosehulman.p2p.protocol.IFilter;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;

public class SendLogFilter implements IFilter {
	
	private List<Request> sendLog;
	private List<ISendLogListener> listeners;
	
	public SendLogFilter() {
		this.sendLog = new ArrayList<>();
		this.listeners = new ArrayList<>();
	}

	@Override
	public void filter(IHost remoteHost, IPacket p) {
		this.logSend(p, remoteHost);
	}
	
	public void logSend(IPacket p, IHost remoteHost) {
		this.sendLog.add(new Request(p, remoteHost));
		this.updateListeners(p, remoteHost);
	}
	
	public List<Request> getSendLog() {
		return this.sendLog;
	}
	
	public void addListener(ISendLogListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ISendLogListener listener) {
		this.listeners.remove(listener);
	}
	
	public void updateListeners(IPacket p, IHost remoteHost) {
		for (ISendLogListener listener : this.listeners) {
			listener.newSendUpdate(p, remoteHost);
		}
	}

}
