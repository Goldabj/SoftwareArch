package edu.rosehulman.p2p.impl;

import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IPacket;

public class Request {
	public IPacket packet;
	public IHost remoteHost;
	
	public Request(IPacket p, IHost rm) {
		this.packet = p;
		this.remoteHost = rm;
	}
}
