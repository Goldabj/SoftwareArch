package edu.rosehulman.p2p.listeners;

import edu.rosehulman.p2p.protocol.IHost;

public interface IConnectionListener extends IListener {
	public void newConnection(IHost remoteHost);
	public void connectionRefused(IHost remoteHost);
}
