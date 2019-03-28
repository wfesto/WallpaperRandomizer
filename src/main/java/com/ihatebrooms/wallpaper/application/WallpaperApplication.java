package com.ihatebrooms.wallpaper.application;

import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
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

//TODO: global hotkeys?
//TODO: white/black listing custom list entries based on time (other conditions?)

public class WallpaperApplication extends Application {

	protected final ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n", Locale.getDefault());
	private static final Logger logger = LogManager.getLogger(WallpaperApplication.class);

	protected Settings savedSettings;
	protected Settings unsavedSettings;
	protected Stage primary;
	protected WallpaperUIElements ui;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primary) throws Exception {
		this.primary = primary;
		String activeProfile = SettingsReaderWriter.getActiveProfile();
		Map<String, Settings> settingsMap = SettingsReaderWriter.readSettings();
		savedSettings = settingsMap.get(activeProfile);
		unsavedSettings = new Settings();
		unsavedSettings.copyFrom(savedSettings);

		logger.trace("Full settings map:\n" + settingsMap.toString());
		logger.debug("Application loading settings:\n" + savedSettings.toString());

		Platform.setImplicitExit(false);

		GridPane rootPane = new GridPane();
		rootPane.setAlignment(Pos.TOP_LEFT);
		rootPane.setHgap(10);
		rootPane.setVgap(10);
		rootPane.setPadding(new Insets(25, 25, 25, 25));

		// TODO: Better resolution handling, possibly saving?
		Scene scene = new Scene(rootPane, 600, 480);
		ui = new WallpaperUIElements(resourceBundle, rootPane, primary);
		ui.initializeState(unsavedSettings);
		ui.addEventProcessors(primary, unsavedSettings, savedSettings);
		primary.setScene(scene);

		SwingUtilities.invokeLater(this::addAppToTray);

		if (((Integer) ui.modeRadioGroup.getSelectedToggle().getUserData()).intValue() != Settings.MODE_SINGLE_FILE) {
			logger.trace("Starting app in non-single file mode");
			ui.saveButton.setDisable(false);
			ui.saveButton.fire();
			ui.saveButton.setDisable(true);
		}
	}

	private void showStage() {
		if (primary != null) {
			primary.show();
			primary.toFront();
		}
	}

	private void addAppToTray() {
		try {
			Toolkit.getDefaultToolkit();
			if (!SystemTray.isSupported()) {
				logger.error("No system tray support, application exiting.");
				Platform.exit();
			}

			SystemTray tray = SystemTray.getSystemTray();
			URL imageUrl = ClassLoader.getSystemResource("images/appIcon.png");
			TrayIcon trayIcon = new TrayIcon(new ImageIcon(imageUrl).getImage(), resourceBundle.getString("tray.icon.label.mouseover"));
			trayIcon.setImageAutoSize(true);

			trayIcon.addActionListener(event -> Platform.runLater(this::showStage));

			final java.awt.PopupMenu popup = new java.awt.PopupMenu();

			MenuItem advanceItem = new MenuItem(resourceBundle.getString("tray.menu.label.nextWallpaper"));
			advanceItem.addActionListener(ae -> {
				ui.advanceButton.fire();
			});

			MenuItem exitItem = new MenuItem(resourceBundle.getString("tray.menu.label.exit"));
			exitItem.addActionListener(ae -> {
				Platform.exit();
				tray.remove(trayIcon);
			});

			MenuItem showItem = new MenuItem(resourceBundle.getString("tray.menu.label.show"));
			showItem.addActionListener(ae -> {
				Platform.runLater(this::showStage);
			});

			popup.add(showItem);
			popup.add(advanceItem);
			popup.add(exitItem);
			trayIcon.setPopupMenu(popup);

			tray.add(trayIcon);
		} catch (java.awt.AWTException e) {
			logger.error("Unable to init system tray");
			logger.error(e.getMessage());
		}
	}
}
