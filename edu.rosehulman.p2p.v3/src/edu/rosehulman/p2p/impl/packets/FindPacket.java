package edu.rosehulman.p2p.impl.packets;

import java.util.List;

import edu.rosehulman.p2p.protocol.AbstractPacket;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IProtocol;

public class FindPacket extends AbstractPacket {

	public FindPacket(String protocol, String object, String host, String port, String fileName, int seqNum,
			int maxDepth, List<IHost> visited, int currentDepth, List<IHost> foundList) {
		super(protocol, IProtocol.FIND, object);
		this.setHeader(IProtocol.HOST, host);
		this.setHeader(IProtocol.PORT, port);
		this.setHeader(IProtocol.FILE_NAME, fileName);
		this.setHeader(IProtocol.SEQ_NUM, seqNum + "");
		this.setHeader(IProtocol.MAX_DEPTH, maxDepth + "");
		this.setHeader(IProtocol.CURRENT_DEPTH, currentDepth + "");
		StringBuilder visitedBuilder = new StringBuilder();
		for (IHost rhost : visited) {
			visitedBuilder.append(rhost.toString());
			visitedBuilder.append(",");
		}
		String visitedString = visitedBuilder.toString().substring(0, visitedBuilder.length() - 1);
		this.setHeader(IProtocol.VISITED, visitedString);
		StringBuilder foundBuilder = new StringBuilder();
		for (IHost rhost : foundList) {
			visitedBuilder.append(rhost.toString());
			visitedBuilder.append(",");
		}
		String foundString = foundBuilder.toString();
		if (!foundString.isEmpty()) {
			foundString = foundString.substring(0, foundBuilder.length() - 1);
		}
				
		this.setHeader(IProtocol.FOUND_LIST, foundString);
		
	}

}
