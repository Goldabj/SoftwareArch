package edu.rosehulman.p2p.protocol;

import java.util.Map;

import edu.rosehulman.p2p.listeners.IListener;

public interface IAction {
	
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException;
	
	public IP2PMediator getMediator();
	public void setMediator(IP2PMediator m);

	public void addListener(IListener listener);
	public void removeListener(IListener listener);
}
