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

import edu.rosehulman.rabbitmq.overstock.model.Item;

public class InventoryProcessor {

	public static void main(String...strings) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(OrderProcessor.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		channel.basicQos(1);
		
		channel.queueDeclare(OrderProcessor.INVENTORY_QUEUE, true, false, false, null);
		channel.queueBind(OrderProcessor.INVENTORY_QUEUE, OrderProcessor.EXCHANGE_NAME, OrderProcessor.INVENTORY_KEY);
		
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				Item item = getObject(body);
				
				try {
					doWork(item);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		channel.basicConsume(OrderProcessor.INVENTORY_QUEUE, false, consumer);
		System.out.println("inventory processor running....");
	}
	
	private static void doWork(Item item) {
		System.out.println("processing item " + item.getId());
	}
	
	private static Item getObject(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream objIn = new ObjectInputStream(bais);
			Item item = (Item) objIn.readObject();
			return item;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
}
