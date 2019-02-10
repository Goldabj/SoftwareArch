package edu.rosehulman.rmiclient.nprime;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;

import edu.rosehulman.rmicommons.Compute;

public class ComputeNPrimes {
	
	public static void main(String args[]) {
        // Need a security manager for RMI to work
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        try {
            // We will use this identifier to query registry service for a registered
            // RMI object under this id. This is how the discovery of a remote object 
            // is achieved. For the discovery to work, a server object must already 
            // be registered with the registry service that we are using.
            String name = "edu.rose-hulman.csse477.rmi";
            
            // First we need to find the registry service. args[0] is a command 
            // line argument that represents hostname of the machine that has
            // the registry service running. We need to supply this hostname
            // while running this client program
            Registry registry = LocateRegistry.getRegistry(args[0]);
            
            // We found registry, now lets lookup our ComputeEngine server object
            Compute comp = (Compute) registry.lookup(name);
            
            // At this point we have a Stub (proxy) of the ComputeEngine
            // Lets create a task to pass to this proxy that will be sent to
            // the real server object trough the RMI protocol and gets executed
            // on the server
            System.out.print("enter in a number: ");
            int num = Integer.parseInt(System.console().readLine());
            NPrimes task = new NPrimes(num);
            String clientIP = java.net.Inet4Address.getLocalHost().getHostAddress().toString();
            NPrimeResponse response = comp.executeTask(task);
            
            // Print the result locally
            System.out.println("clietn IP: " + clientIP);
            System.out.println("server IP: " + response.getServerIP());
            System.out.println("n Primes: ");
            for (Integer n : response.getnPrimes()) {
            		System.out.println("\t" + n);
            }
            
        } catch (Exception e) {
            System.err.println("Execution in the ComputePI RMI Client:");
            e.printStackTrace();
        }
    }    

}
