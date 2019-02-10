package edu.rosehulman.csse477.exam1.server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.rosehulman.csse477.exam1.protocol.CommandPacket;

public class ServerDaemon {

	public static void main(String[] args) throws Exception {
		@SuppressWarnings("resource")
		ServerSocket daemon = new ServerSocket(9999);
		System.out.println("Server waiting for incoming connections ...");
		
		while (true) {
			try {
				Socket clientSocket = daemon.accept();

				ObjectInputStream commandStream = new ObjectInputStream(clientSocket.getInputStream());
				CommandPacket packet = (CommandPacket)commandStream.readObject();
				
				Runnable command = packet.toCommand();
				command.run();
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
