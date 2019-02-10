package edu.rosehulman.p2p.protocol;

import edu.rosehulman.p2p.listeners.IListener;

public abstract class AbstractAction implements IAction {
	private IP2PMediator mediator;
	
	public AbstractAction(IP2PMediator med) {
		this.mediator = med;
	}

	@Override
	public IP2PMediator getMediator() {
		return this.mediator;
	}

	@Override
	public void setMediator(IP2PMediator m) {
		synchronized(this) {
			this.mediator = m;
		}
	}

	@Override
	public void addListener(IListener listener) {
		
	}

	@Override
	public void removeListener(IListener listener) {
		
	}
	
	
	
	
}
