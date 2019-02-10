package edu.rosehulman.p2p.impl.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.P2PMediator.RequestEntry;
import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class FindAction extends AbstractAction {

	public FindAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		int seqNum = Integer.parseInt(p.getHeader(IProtocol.SEQ_NUM));

		RequestEntry entry = mediator.getHostsFromRequestLine(seqNum);
		if (entry != null) {
			entry.hosts.add(remoteHost);
		} else {
			List<IHost> ho = new ArrayList<>();
			ho.add(remoteHost);
			mediator.addToRequestLine(seqNum, p, ho);
		}
		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		p.toStream(monitor.getOutputStream());

	}


}
