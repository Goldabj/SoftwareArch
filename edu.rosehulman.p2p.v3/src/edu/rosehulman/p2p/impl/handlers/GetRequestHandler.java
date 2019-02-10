package edu.rosehulman.p2p.impl.handlers;

import java.util.HashMap;

import edu.rosehulman.p2p.impl.packets.PutPacket;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

public class GetRequestHandler extends AbstractHandler implements IRequestHandler {

	public GetRequestHandler(IP2PMediator mediator) {
		super(mediator);
	}

	@Override
	public void handleRequest(IPacket packet, IHost remoteHost) throws P2PException {
		try {
			int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
			
			String fileName = packet.getHeader(IProtocol.FILE_NAME);
			
			IHost localHost = mediator.getLocalHost();
			
			IPacket putPacket = new PutPacket(IProtocol.PROTOCOL, remoteHost.toString(), localHost.getHostAddress(), localHost.getPort() + "", 
					seqNum, fileName);
			
			mediator.performAction(IProtocol.PUT, putPacket, remoteHost, new HashMap<>());
		}
		catch(Exception e) {
			throw new P2PException(e);
		}
	}

	@Override
	public void addListener(IListener listener) {
	}

	@Override
	public void removeListener(IListener listener) {
	}
}
