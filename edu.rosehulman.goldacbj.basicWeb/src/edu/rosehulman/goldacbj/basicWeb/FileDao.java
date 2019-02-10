package edu.rosehulman.goldacbj.basicWeb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileDao {
	private File file;

	public FileDao(String filePath) {
		file = new File(filePath);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String content = "<h1> sup homies </h1>";
		write(content);
		
	}

	/**
	 * returns the content of the file
	 * 
	 * @return
	 */
	public String getContent() {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				return "";
			}
			return "";
		}
		try {
			
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String content = "";
			String newText = "";
			while ((newText = bufferedReader.readLine()) != null) {
				content += newText;
			}
			bufferedReader.close();
			fileReader.close();
			return content;
		} catch (IOException e) {
			return "";
		} 	
	}

	/**
	 * overwrites the content of the file to the new content.
	 * 
	 * @param content
	 */
	public boolean write(String content) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		PrintWriter writer;
		try {
			writer = new PrintWriter(new FileWriter(file));
			writer.print(content);
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * return if file exists
	 * 
	 * @return
	 */
	public boolean exists() {
		return file.exists();
	}
	
	/**
	 * returns the date the file was last modified.
	 * 
	 * @return
	 */
	public long lastModified() {
		return file.lastModified();
	}
	
	public boolean delete() {
		return file.delete();
	}
	
	

}
