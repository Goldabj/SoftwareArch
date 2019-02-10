package edu.rosehulman.p2p.impl.actions;

import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.P2PException;

public class AttachNOKAction extends AbstractAction {

	public static final String SOCKET_ARG = "SOCKET";
	public static final String SEQNUM_ARG = "SEQNUM";

	public AttachNOKAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		
		Object socket_arg = args.get(SOCKET_ARG);
		if (socket_arg == null || remoteHost == null) {
			return;
		}
		Socket socket = (Socket) socket_arg;

		try {
			p.toStream(socket.getOutputStream());
			socket.close();
			Logger.getGlobal().log(Level.INFO, "Connection rejected to " + remoteHost);
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Could not send attach ok message to remote peer", e);
		}

	}

}
