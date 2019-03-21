package com.ihatebrooms.wallpaper.filter;

import java.io.File;
import java.io.FileFilter;

public class FileImageFilter implements FileFilter {

	private static final String[] imageTypes = {".bmp", ".jpeg", ".jpg", ".png"};

	@Override
	public boolean accept(File arg0) {
		boolean found = false;
		for (int i = 0; i < imageTypes.length && !found; ++i) {
			found = arg0.getName().toLowerCase().endsWith(imageTypes[i]);
		}

		return found;
	}

}
