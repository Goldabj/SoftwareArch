package edu.rosehulman.p2p.listeners;

import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;

public interface ISendLogListener extends IListener {
	public void newSendUpdate(IPacket p, IHost remoteHost);
}
