package edu.rosehulman.GreetingService.impl;

import edu.rosehulman.GreetingService.GreetingService;

public class GreetingServiceImpl implements GreetingService {

	@Override
	public String sayHello() {
		System.out.println("Help, I’m trapped inside" +
				"GreetingServiceImpl!");
		return "Hello World!";
	}

}
