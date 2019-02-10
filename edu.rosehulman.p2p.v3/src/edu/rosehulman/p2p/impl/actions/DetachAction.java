package edu.rosehulman.p2p.impl.actions;

import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class DetachAction extends AbstractAction {
	
	public DetachAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		
		if (remoteHost == null) {
			return;
		}

		if (!mediator.isConnectedToPeer(remoteHost)) {
			return;
		}

		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		mediator.removePeer(remoteHost);
		Socket socket = monitor.getSocket();
		p.toStream(monitor.getOutputStream());

		try {
			socket.close();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.WARNING, "Error closing socket!", e);
		}
	}
}
