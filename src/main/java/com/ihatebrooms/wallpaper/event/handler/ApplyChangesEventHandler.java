package com.ihatebrooms.wallpaper.event.handler;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.EventObject;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.WallpaperUpdater;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.filter.FileImageFilter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyChangesEventHandler implements EventHandler<ActionEvent>, ActionListener {

	private static final Logger logger = LogManager.getLogger(ApplyChangesEventHandler.class);

	protected Settings settings;
	protected static FileImageFilter fileImageFilter = new FileImageFilter();

	@Override
	public void actionPerformed(java.awt.event.ActionEvent event) {
		processEvent(event);
	}

	@Override
	public void handle(ActionEvent arg0) {
		processEvent(arg0);
	}

	protected void processEvent(EventObject eObj) {
		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE && settings.getFilePath() != null) {
			updateWallpaper(settings.getFilePath());
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR && settings.getCurrentDir() != null) {
			List<File> fileList = null;
			try {
			//@formatter:off
			fileList = Files.walk(Paths.get(settings.getCurrentDir()))
				.map(p -> p.toFile())
				.filter(p -> p.isFile())
				.filter(p -> fileImageFilter.accept(p))
				.collect(Collectors.toList());
			//@formatter:on
				logger.trace("Reading dir: " + settings.getCurrentDir());
				logger.trace("Files found: " + fileList.toString());
			} catch (IOException e) {
				logger.error("Unable to iterate directory:");
				logger.error(e.getMessage());
			}

			Random r = new Random();
			File nextWallpaper = fileList.get(r.nextInt(fileList.size()));
			logger.trace("Next wallpaper selected: " + nextWallpaper.getAbsolutePath());
			updateWallpaper(nextWallpaper.getAbsolutePath());
		}
	}

	protected void updateWallpaper(String filePath) {
		WallpaperUpdater.updateWallpaper(filePath);
	}
}
