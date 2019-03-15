package com.ihatebrooms.wallpaper.event.handler;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChoiceEventHandler implements EventHandler<ActionEvent> {

	protected Window parentWindow;
	protected Settings settings;
	protected TextField currentSelectionTextField;
	protected ListView<String> fileListView;

	protected static final String[] imgTypes = {"bmp", "jpeg", "jpg", "png"};

	@Override
	public void handle(ActionEvent arg0) {
		settings.setCurrentDir(null);
		settings.setFilePath(null);
		settings.setFileList(null);

		FileChooser fileChooser = new FileChooser();
		if (settings.getCurrentDir() != null) {
			fileChooser.setInitialDirectory(new File(settings.getCurrentDir()));
		}
		List<String> imgTypeList = new LinkedList<>();
		for (String img : imgTypes) {
			imgTypeList.add("*." + img);
		}
		ExtensionFilter imageExtensionFilter = new ExtensionFilter("Images", imgTypeList);
		fileChooser.getExtensionFilters().add(imageExtensionFilter);

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			File file = fileChooser.showOpenDialog(parentWindow);
			if (file != null && isImage(file)) {
				settings.setFilePath(file.getAbsolutePath());
				settings.setCurrentDir(file.getParentFile().getAbsolutePath());
				currentSelectionTextField.setText(file.getAbsolutePath());
			}
		} else if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			List<File> fileList = fileChooser.showOpenMultipleDialog(parentWindow);
			List<String> pathStringList = fileList.stream().map(s -> s.getAbsolutePath()).collect(Collectors.toList());
			settings.getFileList().addAll(pathStringList);
			fileListView.getItems().addAll(pathStringList);
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			if (settings.getCurrentDir() != null) {
				directoryChooser.setInitialDirectory(new File(settings.getCurrentDir()));
			}
			File file = directoryChooser.showDialog(parentWindow);
			if (file != null) {
				settings.setCurrentDir(file.getAbsolutePath());
				currentSelectionTextField.setText(file.getAbsolutePath());
			}
		}
	}

	protected boolean isImage(File f) {
		int length = f.getName().length();
		String extension = f.getName().substring(length - 3, length);
		return Arrays.binarySearch(imgTypes, extension) > -1;
	}

}
