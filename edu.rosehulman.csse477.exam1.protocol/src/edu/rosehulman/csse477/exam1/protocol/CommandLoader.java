package edu.rosehulman.csse477.exam1.protocol;

public class CommandLoader extends ClassLoader {
	
	public Runnable loadCommand(CommandPacket p) throws InstantiationException, IllegalAccessException {
		String className = p.getClassName();
		byte[] classBytes = p.getClassBytes();
		
		@SuppressWarnings("unchecked")
		Class<? extends Runnable> clazz = (Class<? extends Runnable>)this.defineClass(className, classBytes, 0, classBytes.length);

		this.resolveClass(clazz);
		return clazz.newInstance();
	}
}
