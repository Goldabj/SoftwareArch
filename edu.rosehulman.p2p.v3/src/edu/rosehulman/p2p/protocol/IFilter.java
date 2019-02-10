package edu.rosehulman.p2p.protocol;


public interface IFilter {
	public void filter(IHost remoteHost, IPacket p);
}
