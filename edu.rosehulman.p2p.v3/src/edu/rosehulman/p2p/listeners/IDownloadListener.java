package edu.rosehulman.p2p.listeners;

import edu.rosehulman.p2p.protocol.IHost;

public interface IDownloadListener extends IListener {
	public void downloadComplete(IHost remoteHost, String fileName);
}
