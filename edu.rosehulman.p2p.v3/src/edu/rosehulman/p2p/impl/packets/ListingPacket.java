package edu.rosehulman.p2p.impl.packets;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class ListingPacket extends AbstractPacket {

	public ListingPacket(String protocol, String object, String host, String port, int seqNum) {
		super(protocol, IProtocol.LISTING, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
		this.setHeader(IProtocol.SEQ_NUM, seqNum + "");
		this.setHeader(IProtocol.PAYLOAD_SIZE, 0 + "");
	}
}
