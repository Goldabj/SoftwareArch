package edu.rosehulman.p2p.impl.packets;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class DetachPacket extends AbstractPacket {
	
	public DetachPacket(String protocol, String object, String host, String port) {
		super(protocol, IProtocol.DETACH, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
	}

}
