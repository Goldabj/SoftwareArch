package edu.rosehulman.rmiclient.nprime;

import java.io.Serializable;
import java.util.List;

public class NPrimeResponse implements Serializable {
	
	private static final long serialVersionUID = -4984243083439023354L;
	private List<Integer> nPrimes;
	private String serverIP;
	
	public NPrimeResponse() {
		
	}

	public List<Integer> getnPrimes() {
		return nPrimes;
	}

	public void setnPrimes(List<Integer> nPrimes) {
		this.nPrimes = nPrimes;
	}

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}
	
	

}
