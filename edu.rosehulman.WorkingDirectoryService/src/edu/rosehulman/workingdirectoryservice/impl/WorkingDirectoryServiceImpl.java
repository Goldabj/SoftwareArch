package edu.rosehulman.workingdirectoryservice.impl;

import java.io.File;

import edu.rosehulman.workingdirectoryservice.WorkingDirectoryService;

public class WorkingDirectoryServiceImpl implements WorkingDirectoryService {

	@Override
	public void printWorkingDirectory() {
		File direct = new File(".");
		File[] files = direct.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				System.out.println("File " + files[i].getName());
			}
		}
	}

}
