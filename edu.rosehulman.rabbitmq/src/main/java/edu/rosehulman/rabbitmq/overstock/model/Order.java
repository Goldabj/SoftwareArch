package edu.rosehulman.rabbitmq.overstock.model;

import java.io.Serializable;

public class Order implements Serializable {
	
	private static final long serialVersionUID = -8758579924162288216L;
	int id;
	CreditCard creditCard;
	Item item;
	Shipment shipment;
	
	public Order(int id, CreditCard card, Item item, Shipment shipment) {
		this.id = id;
		this.creditCard = card;
		this.item = item;
		this.shipment = shipment;
	}

	public int getId() {
		return id;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public Item getItem() {
		return item;
	}

	public Shipment getShipment() {
		return shipment;
	}
	
	

}
