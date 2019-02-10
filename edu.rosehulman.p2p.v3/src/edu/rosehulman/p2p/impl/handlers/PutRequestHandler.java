/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Chandan R. Rupakheti (chandan.rupakheti@rose-hulman.edu)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package edu.rosehulman.p2p.impl.handlers;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.P2PMediator.RequestEntry;
import edu.rosehulman.p2p.listeners.IDownloadListener;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.protocol.AbstractHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.P2PException;

/**
 * @author Chandan R. Rupakheti(chandan.rupakheti@rose-hulman.edu)
 *
 */
public class PutRequestHandler extends AbstractHandler implements IRequestHandler {
	
	private List<IDownloadListener> listeners;

	public PutRequestHandler(IP2PMediator mediator) {
		super(mediator);
		this.listeners = new ArrayList<>();
	}

	@Override
	public void handleRequest(IPacket packet, IHost remoteHost) throws P2PException {
		try {
			int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
			
			RequestEntry rPackets = mediator.getHostsFromRequestLine(seqNum);

			String fileName = packet.getHeader(IProtocol.FILE_NAME);
			int size = Integer.parseInt(packet.getHeader(IProtocol.PAYLOAD_SIZE));
			
			if(rPackets == null || rPackets.hosts.isEmpty()) {
				Logger.getGlobal().log(Level.INFO, "Ignoring put response for " + fileName + 
						"! The corresponding get request does not exists." );
				return;
			}
			mediator.completeRequest(seqNum);
			InputStream in = mediator.getPeerStream(remoteHost).getInputStream();
			FileOutputStream fOut = new FileOutputStream(mediator.getRootDirectory() + IProtocol.FILE_SEPERATOR + fileName);
			byte buffer[] = new byte[IProtocol.CHUNK_SIZE];
			int read = 0;
			while(read < size) {
				int currentRead = in.read(buffer);
				fOut.write(buffer, 0, currentRead);
				read += currentRead;
			}
			fOut.close();
			
			this.downloadCompleteUpdate(remoteHost, fileName);
		}
		catch(Exception e) {
			throw new P2PException(e);
		}
	}

	@Override
	public void addListener(IListener listener) {
		if (listener instanceof IDownloadListener) {
			this.listeners.add((IDownloadListener) listener);
		}
		
	}

	@Override
	public void removeListener(IListener listener) {
		if (listener instanceof IDownloadListener) {
			this.listeners.remove((IDownloadListener) listener);
		}
	}
	
	public void downloadCompleteUpdate(IHost remoteHost, String fileName) {
		for (IDownloadListener listener : this.listeners) {
			listener.downloadComplete(remoteHost, fileName);
		}
	}
}
