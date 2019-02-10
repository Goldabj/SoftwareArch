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

import java.util.ArrayList;
import java.util.List;

/**
 * @author rupakhet
 *
 */
public abstract class AbstractHandler implements IRequestHandler {
	protected IP2PMediator mediator;
	private List<IHandler> postHandlers;
	
	public AbstractHandler(IP2PMediator mediator) {
		this.mediator = mediator;
		this.postHandlers = new ArrayList<>();
	}
	
	public IP2PMediator getMediator() {
		return this.mediator;
	}
	
	@Override
	public void handle(IPacket packet, IHost remoteHost) throws P2PException {
		this.handleRequest(packet, remoteHost);
		for (IHandler handler : this.postHandlers) {
			handler.handle(packet, remoteHost); 
		}
	}
	
	public abstract void handleRequest(IPacket packet, IHost remoteHost) throws P2PException;
	
	@Override
	public void addPostResponse(IHandler handler) {
		this.postHandlers.add(handler);
	}
	
	@Override
	public void removePostResponse(IHandler handler) {
		this.postHandlers.remove(handler);
	}
}
