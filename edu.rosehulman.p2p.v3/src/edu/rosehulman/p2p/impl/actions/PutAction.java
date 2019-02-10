package edu.rosehulman.p2p.impl.actions;

import java.io.File;
import java.util.Map;

import edu.rosehulman.p2p.protocol.AbstractAction;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;


public class PutAction extends AbstractAction {
	
	public PutAction(IP2PMediator med) {
		super(med);
	}

	@Override
	public void execute(IHost remoteHost, IPacket p, Map<String, Object> args) throws P2PException {
		IP2PMediator mediator = this.getMediator();
		String file = p.getHeader(IProtocol.FILE_NAME);
		IStreamMonitor monitor = mediator.getPeerStream(remoteHost);
		
		if(monitor == null) {
			throw new P2PException("No connection exists to " + remoteHost);
		}
		
		File fileObj = new File(mediator.getRootDirectory() + IProtocol.FILE_SEPERATOR + file);
		
		if(fileObj.exists() && fileObj.isFile()) {
			p.setHeader(IProtocol.PAYLOAD_SIZE, fileObj.length() + "");
		}
		
		p.toStream(monitor.getOutputStream());
	}

}
