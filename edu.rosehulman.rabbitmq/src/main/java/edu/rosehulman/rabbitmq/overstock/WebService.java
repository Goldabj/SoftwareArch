package edu.rosehulman.rabbitmq.overstock;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.thoughtworks.xstream.XStream;

import edu.rosehulman.rabbitmq.overstock.model.CreditCard;
import edu.rosehulman.rabbitmq.overstock.model.Item;
import edu.rosehulman.rabbitmq.overstock.model.Order;
import edu.rosehulman.rabbitmq.overstock.model.Shipment;

public class WebService {
	
	public static void main(String...strings) throws IOException {
		String xmlOrder = "";
		
		xmlOrder = enterOrder();
		
		sendOrder(xmlOrder);
	}
	
	public static String enterOrder() {
		XStream xstream = new XStream();
		xstream.alias("transaction", Order.class);
		xstream.alias("credit-card", CreditCard.class);
		xstream.alias("item", Item.class);
		xstream.alias("shipment", Shipment.class);
		
		Scanner in = new Scanner(System.in);
		
		System.out.println("Enter Order ID: ");
		int orderId = in.nextInt();
		
		System.out.println("Enter CardID: ");
		int cardId = in.nextInt();
		System.out.println("Enter amount: ");
		int amount = in.nextInt();
		CreditCard card = new CreditCard(cardId, amount);
		
		System.out.println("Enter Item Id: ");
		int itemId = in.nextInt();
		System.out.println("Enter Item quantity: ");
		int quantity = in.nextInt();
		Item item = new Item(itemId, quantity);
		
		System.out.println("Enter ShipmentId: ");
		int shipId = in.nextInt();
		System.out.println("Enter delviery Date: ");
		String date = in.next();
		System.out.println("Enter method Id: ");
		int methodId = in.nextInt();
		Shipment ship = new Shipment(shipId, date, methodId);
		
		Order order = new Order(orderId, card, item, ship);
	
		return xstream.toXML(order);
		
	}
	
	public static void sendOrder(String xmlOrder) throws IOException {
		Socket socket = new Socket("localhost", 8080);
		socket.getOutputStream().write(xmlOrder.getBytes());
		socket.close();
	}

}
