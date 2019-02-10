package edu.rosehulman.p2p.impl.actions;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.StreamMonitor;
import edu.rosehulman.p2p.listeners.IConnectionListener;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;
import edu.rosehulman.p2p.protocol.AbstractPacket;

public class AttachAction extends AbstractAction {

	List<IConnectionListener> listeners;

	public AttachAction(IP2PMediator p2pMediator) {
		super(p2pMediator);
		this.listeners = new ArrayList<>();
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		int seqNum = Integer.parseInt(p.getHeader(IProtocol.SEQ_NUM));

		if (mediator.isConnectedToPeer(remoteHost)) {
			return;
		}

		try {
			List<IHost> hosts = new ArrayList<>();
			hosts.add(remoteHost);
			mediator.addToRequestLine(seqNum, p, hosts);

			Socket socket = new Socket(remoteHost.getHostAddress(), remoteHost.getPort());
			p.toStream(socket.getOutputStream());

			IPacket rPacket = new AbstractPacket();
			rPacket.fromStream(socket.getInputStream());
			if (rPacket.getCommand().equals(IProtocol.ATTACH_OK)) {
				// Connection accepted
				IStreamMonitor monitor = new StreamMonitor(mediator, remoteHost, socket);
				mediator.addPeer(remoteHost, monitor);
				this.newConnectionUpdate(remoteHost);

				// Let's start a thread for monitoring the input stream of this socket
				Thread runner = new Thread(monitor);
				runner.start();
			} else {
				// Connection rejected
				this.connectionRefusedUpdate(remoteHost);
				socket.close();
			}
		} catch (Exception e) {
			Logger.getGlobal().log(Level.SEVERE, "Could not establish connection!", e);
		}

		mediator.removeRequestFromLine(seqNum, remoteHost);
	}

	@Override
	public void addListener(IListener listener) {
		if (listener instanceof IConnectionListener) {
			this.listeners.add((IConnectionListener) listener);
		}

	}

	@Override
	public void removeListener(IListener listener) {
		if (listener instanceof IConnectionListener) {
			this.listeners.add((IConnectionListener) listener);
		}
	}

	private void newConnectionUpdate(IHost host) {
		for (IConnectionListener listener : this.listeners) {
			listener.newConnection(host);
		}
	}
	
	private void connectionRefusedUpdate(IHost host) {
		for (IConnectionListener listener : this.listeners) {
			listener.connectionRefused(host);
		}
	}
}
