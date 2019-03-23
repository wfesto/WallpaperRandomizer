package com.ihatebrooms.wallpaper.event.handler;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.DirectoryWalker;
import com.ihatebrooms.wallpaper.data.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChoiceEventHandler implements EventHandler<ActionEvent> {

	private static final Logger logger = LogManager.getLogger(FileChoiceEventHandler.class);

	protected Window parentWindow;
	protected Settings settings;

	@Override
	public void handle(ActionEvent arg0) {
		FileChooser fileChooser = new FileChooser();
		if (settings.getCurrentDir() != null) {
			fileChooser.setInitialDirectory(new File(settings.getCurrentDir()));
		}

		fileChooser.getExtensionFilters().add(DirectoryWalker.getImageExtensionFilter());

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			File file = fileChooser.showOpenDialog(parentWindow);
			if (file != null && DirectoryWalker.isImage(file)) {
				settings.setFilePath(file.getAbsolutePath());
				settings.setCurrentDir(file.getParentFile().getAbsolutePath());
			}
		} else if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			List<File> fileList = fileChooser.showOpenMultipleDialog(parentWindow);
			if (CollectionUtils.isNotEmpty(fileList)) {
				List<String> pathStringList = fileList.stream().map(s -> s.getAbsolutePath()).collect(toList());
				logger.trace("Adding files:");
				logger.trace(pathStringList.toString());
				settings.addFiles(pathStringList);
				settings.setCurrentDir(new File(pathStringList.get(0)).getParent());
			}
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			directoryChooser.setTitle("Select directory");
			if (settings.getCurrentDir() != null) {
				directoryChooser.setInitialDirectory(new File(settings.getCurrentDir()));
			}
			File file = directoryChooser.showDialog(parentWindow);
			if (file != null) {
				settings.setCurrentDir(file.getAbsolutePath());
				DirectoryWalker.updateSettingsDirectoryFiles(settings);
				settings.resetListIdx();
			}
		}
	}
}
