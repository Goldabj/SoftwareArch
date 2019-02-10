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

package edu.rosehulman.p2p.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.rosehulman.p2p.listeners.IRequestLineListener;
import edu.rosehulman.p2p.protocol.IAction;
import edu.rosehulman.p2p.protocol.IFilter;
import edu.rosehulman.p2p.protocol.IHandler;
import edu.rosehulman.p2p.protocol.IHost;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IPacket;
import edu.rosehulman.p2p.protocol.IRequestHandler;
import edu.rosehulman.p2p.protocol.IStreamMonitor;
import edu.rosehulman.p2p.protocol.P2PException;

public class P2PMediator implements IP2PMediator {
	private volatile Host localhost;
	private String rootDirectory;
	int sequence;
	private Map<IHost, IStreamMonitor> hostToInStreamMonitor;
	private Map<Integer, RequestEntry> requestLine;
	private Map<String, IAction> p2pActions;
	private Map<String, IHandler> requestHandlers;
	private List<IFilter> requestFilters;
	private List<IFilter> sendFilters;
	private List<IRequestLineListener> requestListeners;
	

	public P2PMediator(int port, String rootDirectory) throws UnknownHostException {
		this.rootDirectory = rootDirectory;

		this.localhost = new Host(InetAddress.getLocalHost().getHostAddress(), port);
		this.hostToInStreamMonitor = Collections.synchronizedMap(new HashMap<IHost, IStreamMonitor>());
		
		this.requestLine = Collections.synchronizedMap(new HashMap<>());
		this.sequence = 0;
		
		this.p2pActions = Collections.synchronizedMap(new HashMap<String, IAction>());
		this.requestHandlers = Collections.synchronizedMap(new HashMap<String, IHandler>());
		this.requestFilters = Collections.synchronizedList(new ArrayList<IFilter>());
		this.sendFilters = Collections.synchronizedList(new ArrayList<IFilter>());
		this.requestListeners = Collections.synchronizedList(new ArrayList<>());
		
	}
	
	public synchronized int newSequenceNumber() {
		return ++this.sequence;
	}

	@Override
	public Host getLocalHost() {
		return this.localhost;
	}
	
	@Override
	public String getRootDirectory() {
		return this.rootDirectory;
	}
	
	@Override
	public void addPeer(IHost remoteHost, IStreamMonitor mon) {
		synchronized(this.hostToInStreamMonitor) {
			this.hostToInStreamMonitor.put(remoteHost, mon);
		}
	}

	@Override
	public void removePeer(IHost remoteHost) {
		synchronized (this.hostToInStreamMonitor) {
			this.hostToInStreamMonitor.remove(remoteHost);
		}	
	}


	@Override
	public boolean isConnectedToPeer(IHost remoteHost) {
		return this.hostToInStreamMonitor.containsKey(remoteHost);
	}


	@Override
	public IStreamMonitor getPeerStream(IHost remoteHost) {
		synchronized (this.hostToInStreamMonitor) {
			return this.hostToInStreamMonitor.get(remoteHost);
		}
	}

	@Override
	public void addAction(String command, IAction action) {
		synchronized(this.p2pActions) {
			this.p2pActions.put(command, action);
		}
		
	}

	@Override
	public void removeAction(String command) {
		synchronized (this.p2pActions) {
			this.p2pActions.remove(command);
		}
		
	}

	@Override
	public void removeRequestFromLine(int seqNum, IHost host) {
		synchronized (this.requestLine) {
			RequestEntry entry = this.requestLine.get(seqNum);
			if (entry == null) {
				return;
			}
			List<IHost> hosts = entry.hosts;
			hosts.remove(host);
			if (hosts.isEmpty()) {
				this.requestLine.remove(seqNum);
				requestLineUpdate();
			}
		}
	}

	private void requestLineUpdate() {
		Collection<RequestEntry> requests = this.requestLine.values();
		Collection<IPacket> packets = new ArrayList<>();
		for (RequestEntry r : requests) {
			packets.add(r.packet);
		}
		this.updateListener(packets);
	}

	@Override
	public void addToRequestLine(int seqNum, IPacket packet, List<IHost> hosts) {
		synchronized (this.requestLine) {
			this.requestLine.put(seqNum,  new RequestEntry(packet, hosts));
			requestLineUpdate();
		}
	}

	@Override
	public RequestEntry getHostsFromRequestLine(int seqNum) {
		synchronized (this.requestLine) {
			return this.requestLine.get(seqNum);
		}
	}

	@Override
	public void performAction(String protocolType, IPacket p, IHost remoteHost, Map<String, Object> args) {
		IAction action;
		synchronized (this.p2pActions) {
			action = this.p2pActions.get(protocolType);
		}
		for (IFilter filter : this.sendFilters) {
			filter.filter(remoteHost, p);
		}
		
		if (action == null) {
			return;
		}
		try {
			action.execute(remoteHost, p, args);
		} catch (P2PException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void addRequestHandler(String protocolTyp, IRequestHandler handler) {
		synchronized (this.requestHandlers) {
			this.requestHandlers.put(protocolTyp, handler);
		}
	}

	@Override
	public void handleRequest(String protocolType, IPacket packet, IHost remoteHost) {
		IHandler requestHandler;
		synchronized (this.requestHandlers) {
			requestHandler = this.requestHandlers.get(protocolType);
		}
		for (IFilter rFilter : this.requestFilters) {
			rFilter.filter(remoteHost, packet);
		}
		if (requestHandler == null) {
			return;
		}
		try {
			requestHandler.handle(packet, remoteHost);
		} catch (P2PException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeRequestHanlder(String protocolType) {
		synchronized (this.requestHandlers) {
			this.requestHandlers.remove(protocolType);
		}
		
	}

	@Override
	public void addRequestFiler(IFilter filter) {
		synchronized (this.requestFilters) {
			this.requestFilters.add(filter);
		}
	}

	@Override
	public void removeRequestFiler(IFilter filter) {
		synchronized (this.requestFilters) {
			this.requestFilters.remove(filter);
		}
	}

	@Override
	public void addSendFilter(IFilter filter) {
		synchronized (this.sendFilters) {
			this.sendFilters.add(filter);
		}
		
	}

	@Override
	public void removeSendFilter(IFilter filter) {
		synchronized (this.sendFilters) {
			this.sendFilters.remove(filter);
		}
	}
	
	public class RequestEntry {
		public IPacket packet;
		public List<IHost> hosts;
		
		public RequestEntry(IPacket p, List<IHost> hosts) {
			this.packet = p;
			this.hosts = hosts;
		}
	}

	@Override
	public void addListener(IRequestLineListener listener) {
		synchronized (this.requestListeners) {
			this.requestListeners.add(listener);
		}
	}

	@Override
	public void removeListener(IRequestLineListener listener) {
		synchronized (this.requestListeners) {
			this.requestListeners.remove(listener);
		}
		
	}

	@Override
	public void updateListener(Collection<IPacket> packets) {
		synchronized (this.requestListeners) {
			for (IRequestLineListener listener : this.requestListeners) {
				listener.requestLineUpdated(packets);
			}
		}
		
	}

	@Override
	public List<IHost> getPeers() {
		return new ArrayList<>(this.hostToInStreamMonitor.keySet());
	}

	@Override
	public void completeRequest(int seqNum) {
		synchronized (this.requestLine) {
			this.requestLine.remove(seqNum);
		}
		
	}

}
