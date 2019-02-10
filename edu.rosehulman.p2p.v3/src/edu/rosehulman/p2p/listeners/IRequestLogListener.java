package edu.rosehulman.p2p.listeners;

import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;

public interface IRequestLogListener extends IListener {
	public void newRequestUpdate(IPacket p, IHost remoteHost);
}
