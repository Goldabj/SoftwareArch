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

import edu.rosehulman.rabbitmq.overstock.model.CreditCard;

public class CreditCardProcessor {
	
	public static void main(String...strings) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(OrderProcessor.EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
		channel.basicQos(1);
		
		channel.queueDeclare(OrderProcessor.CREDIT_QUEUE, true, false, false, null);
		channel.queueBind(OrderProcessor.CREDIT_QUEUE, OrderProcessor.EXCHANGE_NAME, OrderProcessor.CREDIT_KEY);
		
		final Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				CreditCard card = getObject(body);
				
				try {
					doWork(card);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println(" [x] Done");
					channel.basicAck(envelope.getDeliveryTag(), false);
				}
			}
		};
		channel.basicConsume(OrderProcessor.CREDIT_QUEUE, false, consumer);
		System.out.println("card processor running....");
	}
	
	private static void doWork(CreditCard card) {
		System.out.println("processing card " + card.getId());
	}
	
	private static CreditCard getObject(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			ObjectInputStream objIn = new ObjectInputStream(bais);
			CreditCard card = (CreditCard) objIn.readObject();
			return card;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
}
