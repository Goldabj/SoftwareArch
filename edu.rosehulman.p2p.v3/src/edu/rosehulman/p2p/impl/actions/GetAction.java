package edu.rosehulman.p2p.impl.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class GetAction extends AbstractAction {
	
	public static final String FILE_NAME_ARG = "FILE_NAME";
	
	public GetAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		int seqNum = Integer.parseInt(p.getHeader(IProtocol.SEQ_NUM));

		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		
		if(monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}
		
		List<IHost> hosts = new ArrayList<>();
		hosts.add(remoteHost);
		mediator.addToRequestLine(seqNum, p, hosts);
		p.toStream(monitor.getOutputStream());

	}

}
