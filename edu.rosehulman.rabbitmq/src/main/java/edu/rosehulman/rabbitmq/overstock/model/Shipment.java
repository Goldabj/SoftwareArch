package edu.rosehulman.rabbitmq.overstock.model;

import java.io.Serializable;

public class Shipment implements Serializable {

	private static final long serialVersionUID = -4588061703227030962L;
	int id;
	String deliverBy;
	int methodId;
	
	public Shipment(int id, String deliverBy, int methodId) {
		this.id = id;
		this.deliverBy = deliverBy;
		this.methodId = methodId;
	}

	public int getId() {
		return id;
	}

	public String getDeliverBy() {
		return deliverBy;
	}

	public int getMethodId() {
		return methodId;
	}
	
	

}
