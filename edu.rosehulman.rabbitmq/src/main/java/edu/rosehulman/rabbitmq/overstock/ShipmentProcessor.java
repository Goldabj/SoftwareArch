package edu.rosehulman.rabbitmq.overstock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import edu.rosehulman.rabbitmq.overstock.model.Shipment;

public class ShipmentProcessor {

	public static void main(String...strings) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(OrderProcessor.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		channel.basicQos(1);
		
		channel.queueDeclare(OrderProcessor.SHIPMENT_QUEUE, true, false, false, null);
		channel.queueBind(OrderProcessor.SHIPMENT_QUEUE, OrderProcessor.EXCHANGE_NAME, OrderProcessor.SHIPMENT_KEY);
		
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				Shipment ship = getObject(body);
				
				try {
					doWork(ship);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		channel.basicConsume(OrderProcessor.SHIPMENT_QUEUE, false, consumer);
		System.out.println("shipment processor running....");
	}
	
	private static void doWork(Shipment ship) {
		System.out.println("processing shipment " + ship.getId());
	}
	
	private static Shipment getObject(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream objIn = new ObjectInputStream(bais);
			Shipment ship = (Shipment) objIn.readObject();
			return ship;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
}
