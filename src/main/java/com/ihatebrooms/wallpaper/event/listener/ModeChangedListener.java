package com.ihatebrooms.wallpaper.event.listener;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModeChangedListener implements ChangeListener<Toggle> {

	protected Settings settings;
	protected ImageView imageView;
	protected TextField currentSelectionTextField;
	protected ListView<String> fileListView;

	@Override
	public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
		boolean showImage = false;
		boolean showFileList = false;

		settings.setCurrentMode(((Integer) newValue.getUserData()).intValue());

		if (newValue.getUserData().equals(Settings.MODE_SINGLE_FILE)) {
			showImage = true;
		} else if (newValue.getUserData().equals(Settings.MODE_MULTI_FILE)) {
			showFileList = true;
		} else if (newValue.getUserData().equals(Settings.MODE_SINGLE_DIR)) {
			showImage = true;
		}

		imageView.setVisible(showImage);
		fileListView.setVisible(showFileList);
	}
}
