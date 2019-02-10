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

package edu.rosehulman.p2p.app;


import javax.swing.JFrame;

import edu.rosehulman.p2p.impl.ConnectionMonitor;
import edu.rosehulman.p2p.impl.P2PMediator;
import edu.rosehulman.p2p.impl.actions.AttachAction;
import edu.rosehulman.p2p.impl.actions.AttachNOKAction;
import edu.rosehulman.p2p.impl.actions.AttachOKAction;
import edu.rosehulman.p2p.impl.actions.DetachAction;
import edu.rosehulman.p2p.impl.actions.FindAction;
import edu.rosehulman.p2p.impl.actions.FoundAction;
import edu.rosehulman.p2p.impl.actions.GetAction;
import edu.rosehulman.p2p.impl.actions.ListAction;
import edu.rosehulman.p2p.impl.actions.ListingAction;
import edu.rosehulman.p2p.impl.actions.PutAction;
import edu.rosehulman.p2p.impl.filters.RequestLogFilter;
import edu.rosehulman.p2p.impl.filters.SendLogFilter;
import edu.rosehulman.p2p.impl.handlers.FindRequestHandler;
import edu.rosehulman.p2p.impl.handlers.FoundRequestHandler;
import edu.rosehulman.p2p.impl.handlers.GetRequestHandler;
import edu.rosehulman.p2p.impl.handlers.ListRequestHandler;
import edu.rosehulman.p2p.impl.handlers.ListingRequestHandler;
import edu.rosehulman.p2p.impl.handlers.PutRequestHandler;
import edu.rosehulman.p2p.impl.handlers.PutResponseHandler;
import edu.rosehulman.p2p.protocol.IAction;
import edu.rosehulman.p2p.protocol.IConnectionMonitor;
import edu.rosehulman.p2p.protocol.IP2PMediator;
import edu.rosehulman.p2p.protocol.IProtocol;
import edu.rosehulman.p2p.protocol.IRequestHandler;

public class P2PApp {
	public static void main(String args[]) throws Exception {
		
		// TODO: fix ATTACH_NOK 
		
		// Initialize the main window
		JFrame mainFrame = new JFrame("P2P Main Window");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		
		// Open configuration window		
		P2PConfigurationWindow configWindow = new P2PConfigurationWindow(mainFrame);
		configWindow.show();
		
		// Get the settings
		String rootDirectory = configWindow.getRootDirectory();
		int port = configWindow.getPort();

		// Configure the main worker that mediates between peers
		IP2PMediator mediator = new P2PMediator(port, rootDirectory);
		IAction attachAction = new AttachAction(mediator);
		mediator.addAction(IProtocol.ATTACH, attachAction);
		mediator.addAction(IProtocol.ATTACH_OK, new AttachOKAction(mediator));
		mediator.addAction(IProtocol.ATTACH_NOK, new AttachNOKAction(mediator));
		mediator.addAction(IProtocol.DETACH, new DetachAction(mediator));
		mediator.addAction(IProtocol.LIST, new ListAction(mediator));
		mediator.addAction(IProtocol.LISTING, new ListingAction(mediator));
		mediator.addAction(IProtocol.GET, new GetAction(mediator));
		mediator.addAction(IProtocol.PUT, new PutAction(mediator));
		mediator.addAction(IProtocol.FIND, new FindAction(mediator));
		mediator.addAction(IProtocol.FOUND, new FoundAction(mediator));

		// setup request handlers
		mediator.addRequestHandler(IProtocol.GET, new GetRequestHandler(mediator));
		IRequestHandler listingHandler = new ListingRequestHandler(mediator);
		mediator.addRequestHandler(IProtocol.LISTING, listingHandler);
		mediator.addRequestHandler(IProtocol.LIST, new ListRequestHandler(mediator));
		IRequestHandler putHandler = new PutRequestHandler(mediator);
		putHandler.addPostResponse(new PutResponseHandler(mediator));
		mediator.addRequestHandler(IProtocol.PUT, putHandler);
		mediator.addRequestHandler(IProtocol.FIND, new FindRequestHandler(mediator));
		IRequestHandler foundHandler = new FoundRequestHandler(mediator);
		mediator.addRequestHandler(IProtocol.FOUND, foundHandler);
		
		// setup Filters for mediator
		RequestLogFilter requestLog = new RequestLogFilter();
		mediator.addRequestFiler(requestLog);
		SendLogFilter sendLog = new SendLogFilter();
		mediator.addSendFilter(sendLog);
		
		// Let's start a connection monitor that listens for incoming connection request
		IConnectionMonitor connectionMonitor = new ConnectionMonitor(mediator);
		Thread runner = new Thread(connectionMonitor);
		runner.start();
		
		// Configure the GUI to receive event notification
		NetworkMapPanel networkPanel = new NetworkMapPanel();
		StatusPanel statusPanel = new StatusPanel();
		PeersPanel peersPanel = new PeersPanel(statusPanel, mediator);
		SearchPanel searchPanel = new SearchPanel(mediator, statusPanel);
		
		putHandler.addListener(statusPanel);
		mediator.addListener(statusPanel);
		listingHandler.addListener(peersPanel);
		requestLog.addListener(peersPanel);
		requestLog.addListener(statusPanel);
		sendLog.addListener(peersPanel);
		foundHandler.addListener(searchPanel);
		putHandler.addListener(searchPanel);
		attachAction.addListener(peersPanel);
		
		P2PGUI gui = new P2PGUI(mainFrame, mediator, connectionMonitor, peersPanel, networkPanel, searchPanel, statusPanel);

		// Show the gui
		gui.show();
	}
	
}
