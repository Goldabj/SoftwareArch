package edu.rosehulman.p2p.impl.actions;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class ListingAction extends AbstractAction {
	
	public ListingAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {	
		IP2PMediator mediator = this.getMediator();
		
		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		
		if(monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}
		
		StringBuilder builder = new StringBuilder();
		File dir = new File(mediator.getRootDirectory());
		for(File f: dir.listFiles()) {
			if(f.isFile()) {
				builder.append(f.getName());
				builder.append(IProtocol.CRLF);
			}
		}
		
		try {
			byte[] payload = builder.toString().getBytes(IProtocol.CHAR_SET);
			
			p.setHeader(IProtocol.PAYLOAD_SIZE, payload.length + "");
			
			OutputStream out = monitor.getOutputStream();
			p.toStream(out);
			out.write(payload);
		}
		catch(Exception e) {
			throw new P2PException(e);
		}
	}

}
