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

package edu.rosehulman.rmiserver;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import edu.rosehulman.rmicommons.Compute;
import edu.rosehulman.rmicommons.Task;

/**
 * This is the server class that accepts incoming task request.
 * Client will implement an instance of Task and send it this server object.
 */
public class ComputeEngine implements Compute {

    public ComputeEngine() {
        super();
    }

    // This method gets called when a client call executeTask(T) on its local
    // stub for this server object
    public <T> T executeTask(Task<T> t) {
        return t.execute();
    }

    public static void main(String[] args) {
        // Through RMI it is possible to access resources on a remote machine.
        // This also means, that we need to implement a security policy to protect
        // unwanted access. Hence, we need to use a security manager with RMI.
        // We will supply a policy file from command-line that will be used by
        // this sercurity manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        
        try {
            // Object locator id
            String name = "edu.rose-hulman.csse477.rmi"; 
            Compute engine = new ComputeEngine();
            
            // This operation is making a regular object an RMI object
            // This object can now accept RMI request, 0 indicates using default port
            Compute stub = (Compute)UnicastRemoteObject.exportObject(engine, 0);
            
            // We will add this object to RMI registry(or naming) service
            // A client will find our Compute stub by querying to this broker
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            
            System.out.println("The RMI server object for ComputeEngine is ready");
            
        } catch (Exception e) {
            System.err.println("Exception in the ComputeEngine Server:");
            e.printStackTrace();
        }
    }
}
