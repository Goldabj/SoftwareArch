package edu.rosehulman.p2p.impl.packets;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IProtocol;

public class GetPacket extends AbstractPacket {

	public GetPacket(String protocol, String object, String host, String port, int seqNum, String fileName) {
		super(protocol, IProtocol.GET, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
		this.setHeader(IProtocol.SEQ_NUM, seqNum + "");
		this.setHeader(IProtocol.FILE_NAME, fileName);
	}
}
