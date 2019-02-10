package edu.rosehulman.p2p.listeners;

import java.util.Collection;

import edu.rosehulman.p2p.protocol.IPacket;

public interface IRequestLineListener extends IListener {
	public void requestLineUpdated(Collection<IPacket> packets);
}
