package edu.rosehulman.rabbitmq.overstock.model;

import java.io.Serializable;

public class CreditCard implements Serializable {

	private static final long serialVersionUID = 3747513907348913290L;
	int id;
	int amount;
	
	public CreditCard(int id, int amount) {
		this.id = id;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public int getAmount() {
		return amount;
	}
	

}
