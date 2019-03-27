package com.ihatebrooms.wallpaper.event.handler;

import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.swing.Timer;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.WallpaperUpdater;
import com.ihatebrooms.wallpaper.data.DirectoryWalker;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class SaveChangesButtonEventHandler implements EventHandler<ActionEvent>, ActionListener {

	private static final Logger logger = LogManager.getLogger(SaveChangesButtonEventHandler.class);

	protected Settings unsavedSettings;
	protected Settings savedSettings;
	protected final Button saveButton;
	protected final Timer timer;

	public SaveChangesButtonEventHandler(Settings unsavedSettings, Settings savedSettings, Button saveButton) {
		this.unsavedSettings = unsavedSettings;
		this.savedSettings = savedSettings;
		this.saveButton = saveButton;
		timer = new Timer(0, this);
	}

	@Override
	public void handle(ActionEvent arg0) {
		if (arg0.getSource() == saveButton) {
			logger.trace("Save event triggered");
			savedSettings = (Settings) unsavedSettings.clone();
			SettingsReaderWriter.writeSettings(savedSettings);
			saveButton.setDisable(true);
		}

		if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_FILE && savedSettings.getFilePath() != null) {
			stopTimer();
			updateWallpaper(savedSettings.getFilePath());
		} else if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_DIR && savedSettings.getCurrentDir() != null) {
			DirectoryWalker.updateSettingsDirectoryFiles(savedSettings);
			configureAndStartTimer(savedSettings.getCalcDelay());
		} else if (savedSettings.getCurrentMode() == Settings.MODE_MULTI_FILE && CollectionUtils.isNotEmpty(savedSettings.getFileList())) {
			configureAndStartTimer(savedSettings.getCalcDelay());
		}
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent arg0) {
		logger.trace("Save action performed");
		if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			return;
		}

		String newWallpaper = null;

		boolean found = false;
		int newIdx = 0;
		while (!found) {
			if (savedSettings.isRandomizeList()) {
				newIdx = new Random(System.currentTimeMillis()).nextInt(savedSettings.getFileList().size());
			} else {
				newIdx = savedSettings.getListIdx();
				++newIdx;
				if (newIdx >= savedSettings.getFileList().size()) {
					newIdx = 0;
				}
			}
			newWallpaper = savedSettings.getFileList().get(newIdx);
			found = isExistingImageFile(newWallpaper);
		}
		// TODO: handle edge case of directory / list with no image files at all
		final String resultingWallpaper = newWallpaper;
		final int resultingIdx = newIdx;
		updateWallpaper(newWallpaper);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				savedSettings.setFilePath(resultingWallpaper);
				savedSettings.setListIdx(resultingIdx);
				SettingsReaderWriter.writeSettings(savedSettings);
			}
		});
	}

	protected void updateWallpaper(String filePath) {
		try {
			WallpaperUpdater.updateWallpaper(filePath);
		} catch (Exception e) {
			logger.error("Error updating wallpaper:");
			logger.error(e.getMessage());
		}
	}

	protected void configureAndStartTimer(int msDelay) {
		stopTimer();
		logger.trace("Starting timer, delay: " + msDelay);
		timer.setInitialDelay(0);
		timer.setDelay(msDelay);
		timer.setRepeats(true);
		timer.start();
	}

	protected void stopTimer() {
		if (this.timer.isRunning()) {
			logger.trace("Stopping timer");
			this.timer.stop();
		}
	}

	protected boolean isExistingImageFile(String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}

		Path filePath = Paths.get(path);
		boolean exists = Files.exists(filePath);
		boolean isImageFile = exists && filePath.toFile().isFile() && DirectoryWalker.isImage(filePath.toFile());

		return isImageFile;
	}
}
