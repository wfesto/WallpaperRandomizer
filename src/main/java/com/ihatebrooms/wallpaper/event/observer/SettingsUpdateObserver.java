package com.ihatebrooms.wallpaper.event.observer;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;
import com.ihatebrooms.wallpaper.data.SettingsReaderWriter;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SettingsUpdateObserver implements Observer {

	private static final Logger logger = LogManager.getLogger(SettingsUpdateObserver.class);

	protected Stage primaryStage;
	protected ImageView previewImageView;

	@Override
	public void update(Observable o, Object arg) {
		try {
			Settings settings = (Settings) o;
			Image image = null;
			if (settings.getFilePath() != null) {
				image = new Image(new FileInputStream(settings.getFilePath()));
			}
			previewImageView.setFitWidth(primaryStage.getWidth() - 80);
			previewImageView.setFitHeight(primaryStage.getHeight() - 200);
			previewImageView.setImage(image);

			Map<String, Settings> settingsMap = new HashMap<String, Settings>();
			settingsMap.put("Default", settings);
			SettingsReaderWriter.writeSettings("Default", settingsMap);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
