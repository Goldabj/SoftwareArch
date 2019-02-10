package edu.rosehulman.csse477.exam1.client;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import edu.rosehulman.csse477.exam1.protocol.CommandPacket;

public class Client {
	private static final String SERVER_HOSTNAME = "localhost"; 
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		System.out.println("Client ready to send commands to the server ...\n");

		sendCommand(PrintCommand.class);
		sendCommand(DateCommand.class);
		
		System.out.println("Press any key to exit ...");
		System.in.read();
	}
	
	public static void sendCommand(Class<? extends Runnable> commandClass) throws UnknownHostException, IOException {
		System.out.println("Sending " + commandClass.getName() + " to the server ...");

		Socket socket = new Socket(SERVER_HOSTNAME, 9999);
		OutputStream socketOut = socket.getOutputStream();
		
		CommandPacket printCommandPacket = new CommandPacket();
		printCommandPacket.fromClass(commandClass);
		
		ObjectOutputStream objectOut = new ObjectOutputStream(socketOut);
		objectOut.writeObject(printCommandPacket);
		socket.close();

		System.out.println("Transfer complete!\n");
	}
	
}
