package edu.rosehulman.p2p.impl.actions;

import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class AttachOKAction extends AbstractAction {

	public static final String SOCKET_ARG = "SOCKET";

	public AttachOKAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		
		Object socket_arg = args.get(SOCKET_ARG);
		if (socket_arg == null || remoteHost == null) {
			return;
		}
		Socket socket = (Socket) socket_arg;

		try {
			p.toStream(socket.getOutputStream());

			IStreamMonitor monitor = new StreamMonitor(mediator, remoteHost, socket);
			mediator.addPeer(remoteHost, monitor);

			// Let's start a thread for monitoring the input stream of this socket
			Thread runner = new Thread(monitor);
			runner.start();
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Could not send attach ok message to remote peer", e);
		}

	}

}
