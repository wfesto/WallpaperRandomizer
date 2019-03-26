package com.ihatebrooms.wallpaper.event.handler;

import com.ihatebrooms.wallpaper.application.ui.WallpaperUIElements;
import com.ihatebrooms.wallpaper.data.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RevertButtonEventHandler implements EventHandler<ActionEvent> {

	protected WallpaperUIElements ui;
	protected Settings unsavedSettings;
	protected Settings savedSettings;

	@Override
	public void handle(ActionEvent arg0) {
		ui.saveButton.setDisable(true);
		ui.initializeState(savedSettings);
		this.unsavedSettings = (Settings) savedSettings.clone();
	}

}
