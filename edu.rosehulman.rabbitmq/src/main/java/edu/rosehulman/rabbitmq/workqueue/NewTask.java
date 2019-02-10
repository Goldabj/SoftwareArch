package edu.rosehulman.rabbitmq.workqueue;

import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class NewTask {

	private final static String QUEUE_NAME = "task_queue";

	public static void main(String[] argv) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		channel.queueDeclare(QUEUE_NAME, true, false, false, null);
		System.out.println("Type messages below. Type quit to exit the program.");
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String message = scanner.nextLine().trim();
			if (message.equalsIgnoreCase("quit"))
				break;
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());

			System.out.println(" [x] Sent '" + message + "'");
		}
		scanner.close();
		channel.close();
		connection.close();
	}
}
