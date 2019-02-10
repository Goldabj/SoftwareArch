package edu.rosehulman.p2p.impl.packets;

import java.util.List;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IProtocol;

public class FoundPacket extends AbstractPacket {

	public FoundPacket(String protocol, String object, String host, String port, String fileName, int seqNum,
			List<IHost> visited, List<IHost> foundList) {
		super(protocol, IProtocol.FOUND, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
		this.setHeader(IProtocol.FILE_NAME, fileName);
		this.setHeader(IProtocol.SEQ_NUM, seqNum + "");
		StringBuilder visitedBuilder = new StringBuilder();
		for (IHost rhost : visited) {
			visitedBuilder.append(rhost.toString());
			visitedBuilder.append(",");
		}
		String visitedString = visitedBuilder.toString().substring(0, visitedBuilder.length() - 1);
		this.setHeader(IProtocol.VISITED, visitedString);
		StringBuilder foundBuilder = new StringBuilder();
		for (IHost rhost : foundList) {
			foundBuilder.append(rhost.toString());
			foundBuilder.append(",");
		}
		String foundString = foundBuilder.toString();
		if (!foundString.isEmpty()) {
			foundString = foundString.substring(0, foundBuilder.length() - 1);
		}
		this.setHeader(IProtocol.FOUND_LIST, foundString);

	}

}
