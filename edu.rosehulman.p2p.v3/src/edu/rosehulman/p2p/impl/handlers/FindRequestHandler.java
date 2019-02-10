package edu.rosehulman.p2p.impl.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.packets.FindPacket;
import edu.rosehulman.p2p.impl.packets.FoundPacket;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

public class FindRequestHandler extends AbstractHandler implements IRequestHandler {

	public FindRequestHandler(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public void addListener(IListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeListener(IListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleRequest(IPacket packet, IHost remoteHost) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		int currentDepth = Integer.parseInt(packet.getHeader(IProtocol.CURRENT_DEPTH));
		int maxDepth = Integer.parseInt(packet.getHeader(IProtocol.MAX_DEPTH));
		String fileName = packet.getHeader(IProtocol.FILE_NAME);
		int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
		List<IHost> visitedHosts = parseHostList(packet.getHeader(IProtocol.VISITED));
		List<IHost> foundHosts = parseHostList(packet.getHeader(IProtocol.FOUND_LIST));
		IHost localHost = mediator.getLocalHost();

		// check for file
		boolean fileFound = false;
		File dir = new File(mediator.getRootDirectory());
		for (File f : dir.listFiles()) {
			if (f.isFile()) {
				if (f.getName().equals(fileName)) {
					fileFound = true;
					break;
				}
			}
		}
		
		if (fileFound) {
			// send found response right away with this as found
			foundHosts.add(localHost);
			IPacket foundPacket = new FoundPacket(IProtocol.PROTOCOL, remoteHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", fileName, 
					seqNum, visitedHosts, foundHosts);
			mediator.performAction(IProtocol.FOUND, foundPacket, remoteHost, new HashMap<>());
			return;
		} else {
			if (currentDepth >= maxDepth) {
				// send found back
				IPacket foundPacket = new FoundPacket(IProtocol.PROTOCOL, remoteHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", fileName, 
						seqNum, visitedHosts, foundHosts);
				mediator.performAction(IProtocol.FOUND, foundPacket, remoteHost, new HashMap<>());
				return;
			} else {
				// send find to all hosts, increase depth, increase visited 
				// log packets
				List<IHost> peers = mediator.getPeers();
				visitedHosts.add(localHost);
				currentDepth++;
				for (IHost rHost : peers) {
					IPacket findPacket = new FindPacket(IProtocol.PROTOCOL, rHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", 
							fileName, seqNum, maxDepth, visitedHosts, currentDepth, foundHosts);
					mediator.performAction(IProtocol.FIND, findPacket, rHost, new HashMap<>());
				}
				
			}
		}

	}

	private List<IHost> parseHostList(String hostList) {
		List<IHost> list = new ArrayList<>();
		if (hostList.isEmpty()) {
			return list;
		}
		String[] hostlists = hostList.split(",");
		for (int i = 0; i < hostlists.length; i++) {
			String host = hostlists[i];
			String[] splitHost = host.split(":");
			String hostName = splitHost[0];
			String hostPort = splitHost[1];
			IHost rHost = new Host(hostName, Integer.parseInt(hostPort));
			list.add(rHost);
		}
		return list;
	}

}
