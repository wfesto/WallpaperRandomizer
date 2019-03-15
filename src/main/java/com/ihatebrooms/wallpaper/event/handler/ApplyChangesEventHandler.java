package com.ihatebrooms.wallpaper.event.handler;

import com.ihatebrooms.wallpaper.application.WallpaperUpdater;
import com.ihatebrooms.wallpaper.data.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyChangesEventHandler implements EventHandler<ActionEvent> {

	protected Settings settings;

	@Override
	public void handle(ActionEvent arg0) {
		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE && settings.getFilePath() != null) {
			WallpaperUpdater.updateWallpaper(settings.getFilePath());
		}
	}

}
