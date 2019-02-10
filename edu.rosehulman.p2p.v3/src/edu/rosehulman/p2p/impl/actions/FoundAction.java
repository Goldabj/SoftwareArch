package edu.rosehulman.p2p.impl.actions;

import java.util.Map;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class FoundAction extends AbstractAction {

	public FoundAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();

		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		p.toStream(monitor.getOutputStream());

	}

}
