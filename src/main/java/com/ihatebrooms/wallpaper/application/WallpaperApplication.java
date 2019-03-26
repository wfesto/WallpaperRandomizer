package com.ihatebrooms.wallpaper.application;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.ui.WallpaperUIElements;
import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

//TODO: tray menu

public class WallpaperApplication extends Application {

	private static final Logger logger = LogManager.getLogger(WallpaperApplication.class);

	protected Settings savedSettings;
	protected Settings unsavedSettings;
	protected Stage primary;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		Platform.setImplicitExit(false);
		this.primary = primary;
		SwingUtilities.invokeLater(this::addAppToTray);

		GridPane rootPane = new GridPane();
		rootPane.setAlignment(Pos.TOP_LEFT);
		rootPane.setHgap(10);
		rootPane.setVgap(10);
		rootPane.setPadding(new Insets(25, 25, 25, 25));

		// TODO: Better resolution handling, possibly saving?
		Scene scene = new Scene(rootPane, 600, 480);
		WallpaperUIElements ui = this.createContent(rootPane, primary);
		primary.setScene(scene);

		if (((Integer) ui.modeRadioGroup.getSelectedToggle().getUserData()).intValue() != Settings.MODE_SINGLE_FILE) {
			logger.trace("Starting app in non-single file mode");
			ui.saveButton.setDisable(false);
			ui.saveButton.fire();
			ui.saveButton.setDisable(true);
		}
	}

	private WallpaperUIElements createContent(GridPane rootPane, Stage primary) throws Exception {
		String activeProfile = SettingsReaderWriter.getActiveProfile();
		Map<String, Settings> settingsMap = SettingsReaderWriter.readSettings();
		savedSettings = settingsMap.get(activeProfile);
		unsavedSettings = (Settings) savedSettings.clone();

		logger.trace("Full settings map:\n" + settingsMap.toString());
		logger.debug("Application loading settings:\n" + savedSettings.toString());

		WallpaperUIElements ui = new WallpaperUIElements(rootPane, primary);
		ui.initializeState(savedSettings);
		ui.addEventProcessors(primary, unsavedSettings, savedSettings);

		return ui;
	}

	private void showStage() {
		if (primary != null) {
			primary.show();
			primary.toFront();
		}
	}

	private void addAppToTray() {
		try {
			java.awt.Toolkit.getDefaultToolkit();
			if (!java.awt.SystemTray.isSupported()) {
				logger.error("No system tray support, application exiting.");
				Platform.exit();
			}

			// set up a system tray icon.
			java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();
			// TODO: find icon
			// TODO: include icon in jar
			URL imageLoc = new URL("file:///E:\\Pictures\\Other\\untitled1.gif");
			java.awt.Image image = ImageIO.read(imageLoc);
			java.awt.TrayIcon trayIcon = new java.awt.TrayIcon(image);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			java.awt.MenuItem exitItem = new java.awt.MenuItem("Exit");
			exitItem.addActionListener(event -> {
				Platform.exit();
				tray.remove(trayIcon);
			});

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

			tray.add(trayIcon);
		} catch (java.awt.AWTException | IOException e) {
			logger.error("Unable to init system tray");
			logger.error(e.getMessage());
		}
	}
}
