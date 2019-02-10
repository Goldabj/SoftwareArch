package edu.rosehulman.csse477.exam1.protocol;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class CommandPacket implements Serializable{
	private static final long serialVersionUID = 2702480134487410545L;

	private String className;
	private byte[] classBytes;

	public CommandPacket() {
	}
	
	public String getClassName() {
		return className;
	}

	public byte[] getClassBytes() {
		return classBytes;
	}

	public void fromClass(Class<?> commandClass){
		this.className = commandClass.getName();

		InputStream in = commandClass.getResourceAsStream(commandClass.getSimpleName() + ".class");
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			int aByte;
			while((aByte = in.read()) != -1){
				out.write(aByte);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.classBytes = out.toByteArray();
	}
	
	public Runnable toCommand() throws InstantiationException, IllegalAccessException {
		CommandLoader classLoader = new CommandLoader();
		return classLoader.loadCommand(this);
	}
}
