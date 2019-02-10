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

package edu.rosehulman.p2p.protocol;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.impl.P2PMediator.RequestEntry;
import edu.rosehulman.p2p.listeners.IRequestLineListener;

public interface IP2PMediator {
	
	// getters/settes
	public IHost getLocalHost();
	public String getRootDirectory();
	public int newSequenceNumber();
	
	// peer control API
	public void addPeer(IHost remoteHost, IStreamMonitor monitor);
	public void removePeer(IHost remoteHost);
	public boolean isConnectedToPeer(IHost remoteHost);
	public IStreamMonitor getPeerStream(IHost remoteHost);
	public List<IHost> getPeers();
	
	// RequestLine API (a line for waiting for expected requests)
	public void removeRequestFromLine(int seqNum, IHost host);
	public void addToRequestLine(int seqNum, IPacket p, List<IHost> host);
	public RequestEntry getHostsFromRequestLine(int seqNum);
	public void completeRequest(int seqNum);
	
	// Actions (send handler) API
	public void addAction(String protocolType, IAction action);
	public void performAction(String protocolType, IPacket p, IHost remoteHost, Map<String, Object> args);
	public void removeAction(String command);
	
	// Request Handler API
	public void addRequestHandler(String protocolTyp, IRequestHandler handler);
	public void handleRequest(String protocolType, IPacket packet, IHost remoteHost);
	public void removeRequestHanlder(String protocolType);
	
	// Filters API
	public void addRequestFiler(IFilter filter);
	public void removeRequestFiler(IFilter filter);
	public void addSendFilter(IFilter filter);
	public void removeSendFilter(IFilter filter);
	
	// Request listener API
	public void addListener(IRequestLineListener listener);
	public void removeListener(IRequestLineListener listener);
	public void updateListener(Collection<IPacket> packets);

}
