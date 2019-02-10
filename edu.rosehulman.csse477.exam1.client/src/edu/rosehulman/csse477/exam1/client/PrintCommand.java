package edu.rosehulman.csse477.exam1.client;
import java.io.Serializable;

public class PrintCommand implements Runnable, Serializable {
	private static final long serialVersionUID = 1791405668915963950L;
	private String message;

	public PrintCommand() {
		this.message = "Hello world!";
	}

	public PrintCommand(String msg) {
		this.message = msg;
	}

	@Override
	public void run() {
		System.out.println("\n------------------------------------------------------");
		System.out.println("Executing a simple print command ...");
		System.out.println("------------------------------------------------------");

		System.out.println(message);
	}
}
