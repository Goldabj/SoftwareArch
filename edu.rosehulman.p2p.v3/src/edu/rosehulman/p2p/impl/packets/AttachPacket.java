package edu.rosehulman.p2p.impl.packets;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class AttachPacket extends AbstractPacket {
	
	public AttachPacket(String protocol, String object, String host, String port, int seqNum) {
		super(protocol, IProtocol.ATTACH, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
		this.setHeader(IProtocol.SEQ_NUM, seqNum + "");
	}

}
