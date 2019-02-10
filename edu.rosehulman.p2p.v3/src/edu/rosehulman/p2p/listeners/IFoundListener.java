package edu.rosehulman.p2p.listeners;

import java.util.List;

import edu.rosehulman.p2p.protocol.IHost;

public interface IFoundListener extends IListener {
	public void fileFoundUpdate(String fileName, List<IHost> foundHost);
}
