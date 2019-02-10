package edu.rosehulman.rmiclient.nprime;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.rosehulman.rmicommons.Task;

public class NPrimes implements Task<NPrimeResponse>, Serializable {
	private static final long serialVersionUID = 1987L;
	int n; 
	
	public NPrimes(int n) {
		this.n = n;
	}
	
	public NPrimes() {
		
	}

	@Override
	public NPrimeResponse execute() {
		NPrimeResponse response = new NPrimeResponse();
		
		try {
			String serverIP = java.net.Inet4Address.getLocalHost().toString();
			response.setServerIP(serverIP);
		} catch (UnknownHostException e) {
			System.out.println("cannot retrive IP address");
		}
		response.setnPrimes(computeNPrimes());
		return response;
	}
	
	public List<Integer> computeNPrimes() {
		int count = 0; 
		List<Integer> primeNums = new ArrayList<>();
		int num = 3;
		while (count < this.n) {
			boolean prime = true;
			for (int i = 2 ; i <= Math.sqrt(num); i++) {
				if (num % i == 0) {
					prime = false;
					break;
				}
			}
			if (prime) {
				primeNums.add(num);
				count++;
			}
			num++;
		}
		return primeNums;
	}

}
