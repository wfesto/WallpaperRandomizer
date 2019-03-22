package com.ihatebrooms.wallpaper.event.observer;

import java.io.FileInputStream;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SettingsUpdateObserver implements Observer {

	private static final Logger logger = LogManager.getLogger(SettingsUpdateObserver.class);

	protected Stage primaryStage;
	protected ImageView previewImageView;
	protected TextField currentSelectionTextField;
	protected ListView<String> fileListView;

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		Settings settings = (Settings) o;

		currentSelectionTextField.setText(settings.getFilePath());

		boolean showImage = false;
		boolean showFileList = false;

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			showImage = true;
			currentSelectionTextField.setText(settings.getFilePath());
			fileListView.getItems().clear();
			settings.setFileList(null);
		} else if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			showFileList = true;
			if (arg != null && arg instanceof List) {
				fileListView.getItems().addAll((List<String>) arg);
			}
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			showImage = true;
			currentSelectionTextField.setText(settings.getCurrentDir());
			fileListView.getItems().clear();
			fileListView.getItems().addAll(settings.getFileList());
		}

		previewImageView.setVisible(showImage);
		fileListView.setVisible(showFileList);

		try {
			Image image = null;
			if (settings.getFilePath() != null) {
				image = new Image(new FileInputStream(settings.getFilePath()));
			}

			previewImageView.setImage(image);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
