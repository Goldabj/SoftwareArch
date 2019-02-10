package edu.rosehulman.p2p.listeners;

import java.util.List;

import edu.rosehulman.p2p.protocol.IHost;

public interface IListingListener extends IListener{
	public void listingReceived(IHost remoteHost, List<String> listing);
}
