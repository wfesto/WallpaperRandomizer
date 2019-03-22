package com.ihatebrooms.wallpaper.event.handler;

import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.Timer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.WallpaperUpdater;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.Data;

@Data
public class SaveChangesButtonEventHandler implements EventHandler<ActionEvent>, ActionListener {

	private static final Logger logger = LogManager.getLogger(SaveChangesButtonEventHandler.class);

	protected Settings settings;
	protected Timer timer;

	public SaveChangesButtonEventHandler(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void handle(ActionEvent arg0) {
		SettingsReaderWriter.writeSettings(settings);

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE && settings.getFilePath() != null) {
			updateWallpaper(settings.getFilePath());
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR && settings.getCurrentDir() != null) {
			createAndStartTimer(settings.getChangeDelay());
		} else if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE && CollectionUtils.isNotEmpty(settings.getFileList())) {
			createAndStartTimer(settings.getChangeDelay());
		}
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent arg0) {
		int newIdx = 0;
		if (settings.isRandomizeList()) {
			newIdx = new Random(System.currentTimeMillis()).nextInt(settings.getFileList().size());
		} else {
			newIdx = settings.getListIdx();
			++newIdx;
			if (newIdx >= settings.getFileList().size()) {
				newIdx = 0;
			}
			settings.setListIdx(newIdx);
			SettingsReaderWriter.writeSettings(settings);
		}

		updateWallpaper(settings.getFileList().get(newIdx));
	}

	protected void updateWallpaper(String filePath) {
		try {
			WallpaperUpdater.updateWallpaper(filePath);
		} catch (Exception e) {
			logger.error("Error updating wallpaper:");
			logger.error(e.getMessage());
		}
	}

	protected void createAndStartTimer(int msDelay) {
		stopTimer();
		timer = new Timer(settings.getChangeDelay(), this);
		timer.setRepeats(true);
		timer.start();
	}

	protected void stopTimer() {
		if (this.timer != null && this.timer.isRunning()) {
			this.timer.stop();
		}
	}
}
