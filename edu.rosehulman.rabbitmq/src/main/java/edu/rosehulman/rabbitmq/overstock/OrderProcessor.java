package edu.rosehulman.rabbitmq.overstock;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.thoughtworks.xstream.XStream;

import edu.rosehulman.rabbitmq.overstock.model.CreditCard;
import edu.rosehulman.rabbitmq.overstock.model.Item;
import edu.rosehulman.rabbitmq.overstock.model.Order;
import edu.rosehulman.rabbitmq.overstock.model.Shipment;

public class OrderProcessor {

	public static final String EXCHANGE_NAME = "Order-Processing-Excahnge";
	public static final String CREDIT_QUEUE = "Credit-Processing-Queue";
	public static final String INVENTORY_QUEUE = "Inventory-Processing-Queue";
	public static final String SHIPMENT_QUEUE = "Shipment-Processing-Queue";

	public static final String CREDIT_KEY = "CREDIT";
	public static final String INVENTORY_KEY = "INVENTORY";
	public static final String SHIPMENT_KEY = "SHIPMENT";

	private static Channel channel;

	public static void main(String... strings) throws IOException, TimeoutException {
		// setup up multiple queues and
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		channel.basicQos(1);

		// declare queues
		channel.queueDeclare(CREDIT_QUEUE, true, false, false, null);
		channel.queueDeclare(INVENTORY_QUEUE, true, false, false, null);
		channel.queueDeclare(SHIPMENT_QUEUE, true, false, false, null);

		// bind queues
		channel.queueBind(CREDIT_QUEUE, EXCHANGE_NAME, CREDIT_KEY);
		channel.queueBind(INVENTORY_QUEUE, EXCHANGE_NAME, INVENTORY_KEY);
		channel.queueBind(SHIPMENT_QUEUE, EXCHANGE_NAME, SHIPMENT_KEY);
		
		System.out.println("order processor running....");
		
		// fake HTTP endpoint
		while(true) {
			ServerSocket server;
			try {
				server = new ServerSocket(8080);
			}
			catch(Exception e) {
				e.printStackTrace();
				return;
			}
			
			while(true) {
				try {
					Socket client = server.accept();
					
					BufferedReader input =
				            new BufferedReader(new InputStreamReader(client.getInputStream()));
					String xml = "";
					String nextLine = "";
					while((nextLine = input.readLine()) != null) {
						xml += nextLine;
					}
					processOrder(xml);
					
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void processOrder(String xmlOrder) throws IOException {
		XStream xstream = new XStream();
		xstream.alias("transaction", Order.class);
		xstream.alias("credit-card", CreditCard.class);
		xstream.alias("item", Item.class);
		xstream.alias("shipment", Shipment.class);
		Order order = (Order) xstream.fromXML(xmlOrder);

		channel.basicPublish(EXCHANGE_NAME, CREDIT_KEY, MessageProperties.PERSISTENT_BASIC, getBytes(order.getCreditCard()));
		channel.basicPublish(EXCHANGE_NAME, INVENTORY_KEY, MessageProperties.PERSISTENT_BASIC, getBytes(order.getItem()));
		channel.basicPublish(EXCHANGE_NAME, SHIPMENT_KEY, MessageProperties.PERSISTENT_BASIC, getBytes(order.getShipment()));
		System.out.println("processing order...");
	}

	private static byte[] getBytes(Serializable obj) {
		byte[] bytes;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.flush();
			oos.reset();
			bytes = baos.toByteArray();
			oos.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
			bytes = new byte[] {};
		}
		return bytes;
	}

}
