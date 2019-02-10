/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package edu.rosehulman.rmiclient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.math.BigDecimal;
import edu.rosehulman.rmicommons.Compute;

/**
 * This is the entry method for our client that sets up security manager,
 * locates a server object from the registry, creates the PI computation task,
 * executes the task in server, and prints the result.
 */
public class ComputePi {
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
            Pi task = new Pi(Integer.parseInt(args[1]));
            BigDecimal pi = comp.executeTask(task);
            
            // Print the result locally
            System.out.println(pi);
            
        } catch (Exception e) {
            System.err.println("Execution in the ComputePI RMI Client:");
            e.printStackTrace();
        }
    }    
}
