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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.rosehulman.p2p.impl.P2PMediator.RequestEntry;
import edu.rosehulman.p2p.listeners.IListener;
import edu.rosehulman.p2p.listeners.IListingListener;
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
public class ListingRequestHandler extends AbstractHandler implements IRequestHandler {
	
	private List<IListingListener> listeners;

	public ListingRequestHandler(IP2PMediator mediator) {
		super(mediator);
		this.listeners = new ArrayList<>();
	}

	@Override
	public void handleRequest(IPacket packet, IHost remoteHost) throws P2PException {
	
		int seqNum = Integer.parseInt(packet.getHeader(IProtocol.SEQ_NUM));
		int payloadSize = Integer.parseInt(packet.getHeader(IProtocol.PAYLOAD_SIZE));
		
		RequestEntry rPackets = mediator.getHostsFromRequestLine(seqNum);
		if(rPackets == null || rPackets.hosts.isEmpty()) {
			Logger.getGlobal().log(Level.INFO, "Ignoring listing response! The corresponding list request does not exists." );
			return;	
		}
		mediator.completeRequest(seqNum);
		
		try {
			InputStream in = mediator.getPeerStream(remoteHost).getInputStream();
			List<String> listing = new ArrayList<>();

			byte[] buffer = new byte[payloadSize];
			in.read(buffer);
			
			String listingStr = new String(buffer, IProtocol.CHAR_SET);
			StringTokenizer tokenizer = new StringTokenizer(listingStr);
			while(tokenizer.hasMoreTokens()) {
				String file = tokenizer.nextToken(IProtocol.LF).trim();
				if(!file.isEmpty()) {
					listing.add(file);
				}
			}
			
			this.updateListeners(remoteHost, listing);
		}
		catch(Exception e) {
			throw new P2PException(e);
		}
	}
	
	public void updateListeners(IHost remoteHost, List<String> listing) {
		for (IListingListener listener : this.listeners) {
			listener.listingReceived(remoteHost, listing);
		}
	}

	@Override
	public void addListener(IListener listener) {
		if (listener instanceof IListingListener) {
			this.listeners.add((IListingListener) listener);
		}
	}

	@Override
	public void removeListener(IListener listener) {
		if (listener instanceof IListingListener) {
			this.listeners.remove((IListingListener) listener);
		}
		
	}
}
