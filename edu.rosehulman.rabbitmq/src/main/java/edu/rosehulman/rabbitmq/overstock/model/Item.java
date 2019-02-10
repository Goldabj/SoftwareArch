package edu.rosehulman.rabbitmq.overstock.model;

import java.io.Serializable;

public class Item implements Serializable {
	
	private static final long serialVersionUID = 7107730646399087834L;
	int id;
	int quantity;
	
	public Item(int id, int quantity) {
		this.id = id;
		this.quantity = quantity;
	}

	public int getId() {
		return id;
	}

	public int getQuantity() {
		return quantity;
	}
	
	

}
