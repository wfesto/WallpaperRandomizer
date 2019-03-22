package com.ihatebrooms.wallpaper.event.handler;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.WallpaperUpdater;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaveChangesButtonEventHandler implements EventHandler<ActionEvent> {

	private static final Logger logger = LogManager.getLogger(SaveChangesButtonEventHandler.class);

	protected Settings settings;

	@Override
	public void handle(ActionEvent arg0) {
		SettingsReaderWriter.writeSettings(settings);

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE && settings.getFilePath() != null) {
			updateWallpaper(settings.getFilePath());
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR && settings.getCurrentDir() != null) {
			List<File> fileList = null;
			Random r = new Random();
			File nextWallpaper = fileList.get(r.nextInt(fileList.size()));
			logger.trace("Next wallpaper selected: " + nextWallpaper.getAbsolutePath());
		}

	}

	protected void updateWallpaper(String filePath) {
		WallpaperUpdater.updateWallpaper(filePath);
	}
}
