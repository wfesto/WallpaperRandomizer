package com.ihatebrooms.wallpaper.event.handler;

import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class SaveChangesButtonEventHandler implements EventHandler<ActionEvent>, ActionListener {

	private static final Logger logger = LogManager.getLogger(SaveChangesButtonEventHandler.class);

	protected final Settings unsavedSettings;
	protected final Settings savedSettings;
	protected final Button saveButton;
	protected final Timer timer;
	protected final ResourceBundle resourceBundle;

	public SaveChangesButtonEventHandler(Settings unsavedSettings, Settings savedSettings, Button saveButton, ResourceBundle resourceBundle) {
		this.unsavedSettings = unsavedSettings;
		this.savedSettings = savedSettings;
		this.saveButton = saveButton;
		timer = new Timer(0, this);
		this.resourceBundle = resourceBundle;
	}

	@Override
	public void handle(ActionEvent arg0) {
		if (arg0.getSource() == saveButton) {
			if (unsavedSettings.getCalcDelay() <= 0) {
				logger.error("Cannot have error <= 0");
				Alert errorAlert = new Alert(AlertType.ERROR);
				errorAlert.setHeaderText(resourceBundle.getString("alert.invalidDelay.header"));
				errorAlert.setContentText(resourceBundle.getString("alert.invalidDelay.text"));
				errorAlert.showAndWait();
			} else {
				logger.trace("Save event triggered");
				savedSettings.copyFrom(unsavedSettings);
				SettingsReaderWriter.writeSettings(savedSettings);
				saveButton.setDisable(true);
			}
		}

		if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_FILE && savedSettings.getFilePath() != null) {
			stopTimer();
			updateWallpaper(savedSettings.getFilePath());
		} else if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_DIR && savedSettings.getCurrentDir() != null) {
			configureAndStartTimer(savedSettings.getCalcDelay());
		} else if (savedSettings.getCurrentMode() == Settings.MODE_MULTI_FILE && CollectionUtils.isNotEmpty(savedSettings.getFileList())) {
			configureAndStartTimer(savedSettings.getCalcDelay());
		}
	}

	@Override
	public void actionPerformed(java.awt.event.ActionEvent arg0) {
		logger.trace("Save action performed");
		List<String> fileList = null;

		if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			return;
		} else if (savedSettings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			fileList = DirectoryWalker.readDirectoryImageFiles(savedSettings.getCurrentDir(), savedSettings.isRecurseSubDirs());
		} else if (savedSettings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			fileList = savedSettings.getFileList();
		}

		String newWallpaper = null;

		boolean found = false;
		int newIdx = 0;
		while (!found) {
			if (savedSettings.isRandomizeList()) {
				newIdx = new Random(System.currentTimeMillis()).nextInt(fileList.size());
			} else {
				newIdx = savedSettings.getListIdx();
				++newIdx;
				if (newIdx >= fileList.size()) {
					newIdx = 0;
				}
			}
			newWallpaper = fileList.get(newIdx);
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
