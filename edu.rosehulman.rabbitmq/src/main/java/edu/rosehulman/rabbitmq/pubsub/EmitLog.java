package edu.rosehulman.rabbitmq.pubsub;

import java.util.Scanner;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

public class EmitLog {

	private static final String EXCHANGE_NAME = "logs";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
		System.out.println("Type messages below. Type quit to exit the program.");
		
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String message = scanner.nextLine().trim();
			if (message.equalsIgnoreCase("quit"))
				break;
			channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
			System.out.println(" [x] Sent '" + message + "'");
		}
		scanner.close();
		channel.close();
		connection.close();
	}

}
