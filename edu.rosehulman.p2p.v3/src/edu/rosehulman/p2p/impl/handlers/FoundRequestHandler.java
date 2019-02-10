package edu.rosehulman.p2p.impl.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.rosehulman.p2p.impl.Host;
import edu.rosehulman.p2p.impl.P2PMediator.RequestEntry;
import edu.rosehulman.p2p.impl.packets.FoundPacket;
import edu.rosehulman.p2p.listeners.IFoundListener;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

public class FoundRequestHandler extends AbstractHandler implements IRequestHandler {
	
	private List<IFoundListener> listeners;
	private Map<Integer, List<IHost>> foundHostMap;

	public FoundRequestHandler(IP2PMediator mediator) {
		super(mediator);
		this.listeners = new ArrayList<>();
		this.foundHostMap = new HashMap<>();
	}

	@Override
	public void addListener(IListener listener) {
		if (listener instanceof IFoundListener) {
			this.listeners.add((IFoundListener) listener);
		}
		
	}

	@Override
	public void removeListener(IListener listener) {
		if (listener instanceof IFoundListener) {
			this.listeners.remove((IFoundListener) listener);
		}
	}
	
	public void fileFoundUpdate(String fileName, List<IHost> foundHost) {
		Set<IHost> hs = new HashSet<>();
		hs.addAll(foundHost);
		foundHost.clear();
		foundHost.addAll(hs);
		
		for (IFoundListener listener : this.listeners) {
			listener.fileFoundUpdate(fileName, foundHost);
		}
	}

	@Override
	public void handleRequest(IPacket packet, IHost remoteHost) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
		String fileName = packet.getHeader(IProtocol.FILE_NAME);
		List<IHost> visitedHosts = parseHostList(packet.getHeader(IProtocol.VISITED));
		List<IHost> foundHosts = parseHostList(packet.getHeader(IProtocol.FOUND_LIST));
		IHost localHost = mediator.getLocalHost();
		
		RequestEntry entry = mediator.getHostsFromRequestLine(seqNum);
		if (entry == null) {
			return;
		}
		
		List<IHost> foundList = this.foundHostMap.get(seqNum);
		if (foundList != null) {
			foundList.addAll(foundHosts);
		} else {
			this.foundHostMap.put(seqNum, foundHosts);
			foundList = foundHosts;
		}
		
		
		if (visitedHosts.size() == 1) {
			// find was originated here 
			this.fileFoundUpdate(fileName, foundList);
			mediator.completeRequest(seqNum);
			return;
		} else {
			visitedHosts.remove(visitedHosts.size() - 1);
			IHost nextHost = visitedHosts.get(visitedHosts.size() - 1);
			entry.hosts.remove(remoteHost);
			if (entry.hosts.isEmpty()) {
				this.mediator.completeRequest(seqNum);
				IPacket foundPacket = new FoundPacket(IProtocol.PROTOCOL, nextHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", 
						fileName, seqNum, visitedHosts, foundList);
				mediator.performAction(IProtocol.FOUND, foundPacket, nextHost, new HashMap<>());
				return;
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
