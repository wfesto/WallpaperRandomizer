package com.ihatebrooms.wallpaper.event.observer;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.scene.control.ListView;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SettingsUpdateObserver implements Observer {

	private static final Logger logger = LogManager.getLogger(SettingsUpdateObserver.class);

	protected ListView<String> fileListView;

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		Settings settings = (Settings) o;

		if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			if (arg != null && arg instanceof List) {
				fileListView.getItems().addAll((List<String>) arg);
			}
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			fileListView.getItems().clear();
			fileListView.getItems().addAll(settings.getFileList());
		}
	}
}
