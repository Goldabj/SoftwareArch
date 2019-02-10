package edu.rosehulman.csse477.exam1.client;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;

public class DateCommand implements Runnable, Serializable {
	private static final long serialVersionUID = 5639897389482743511L;

	@Override
	public void run() {
		System.out.println("\n------------------------------------------------------");
		System.out.println("Executing the date command ...");
		System.out.println("------------------------------------------------------");
		
		try {
			String command = "date";
			
			if(System.getProperty("os.name").toLowerCase().startsWith("win")) {
				command += " /T";
			}
			
			Process systemProcess = Runtime.getRuntime().exec(command);
			InputStream in = systemProcess.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
