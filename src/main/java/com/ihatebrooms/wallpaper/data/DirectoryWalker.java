package com.ihatebrooms.wallpaper.data;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.stage.FileChooser.ExtensionFilter;

public class DirectoryWalker {

	private static final Logger logger = LogManager.getLogger(DirectoryWalker.class);
	protected static final String[] imgTypes = {"bmp", "jpeg", "jpg", "png"};

	public static void updateSettingsDirectoryFiles(Settings settings) {
		List<String> fileList = null;
		settings.setCurrentDir(settings.getCurrentDir());
		try {
			int walkDepth = settings.isRecurseSubDirs() ? Integer.MAX_VALUE : 1;
			//@formatter:off
			fileList = Files.walk(Paths.get(settings.getCurrentDir()), walkDepth)
				.map(p -> p.toFile())
				.filter(p -> p.isFile())
				.filter(p -> isImage(p))
				.map(p -> p.getAbsolutePath())
				.collect(toList());
			//@formatter:on
			logger.trace("Reading dir: " + settings.getCurrentDir());
			logger.trace("Files found: " + fileList.toString());
		} catch (IOException e) {
			logger.error("Unable to iterate directory:");
			logger.error(e.getMessage());
		}

		settings.setFileList(fileList);
	}

	public static ExtensionFilter getImageExtensionFilter() {
		return new ExtensionFilter("Images", Arrays.stream(imgTypes).map(p -> "*.".concat(p)).collect(toList()));
	}

	public static boolean isImage(File f) {
		boolean found = false;
		for (int i = 0; i < imgTypes.length && !found; ++i) {
			found = f.getName().toLowerCase().endsWith(imgTypes[i].toLowerCase());
		}
		return found;
	}

}
