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
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChoiceEventHandler implements EventHandler<ActionEvent> {

	private static final Logger logger = LogManager.getLogger(FileChoiceEventHandler.class);

	protected final Window parentWindow;
	protected final Settings settings;
	protected final Button saveButton;

	@Override
	public void handle(ActionEvent arg0) {
		FileChooser fileChooser = new FileChooser();
		if (settings.getCurrentDir() != null) {
			fileChooser.setInitialDirectory(new File(settings.getCurrentDir()));
		}
		boolean selectionMade = false;

		fileChooser.getExtensionFilters().add(DirectoryWalker.getImageExtensionFilter());

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			File file = fileChooser.showOpenDialog(parentWindow);
			if (file != null && DirectoryWalker.isImageFile(file)) {
				settings.setFilePath(file.getAbsolutePath());
				settings.setCurrentDir(file.getParentFile().getAbsolutePath());
				selectionMade = true;
			}
		} else if (settings.getCurrentMode() == Settings.MODE_MULTI_FILE) {
			List<File> fileList = fileChooser.showOpenMultipleDialog(parentWindow);
			if (CollectionUtils.isNotEmpty(fileList)) {
				//@formatter:off
				List<String> pathStringList =
						fileList.stream()
						.map(s -> s.getAbsolutePath())
						.filter(s -> settings.isAllowDuplicates() || !settings.getFileList().contains(s))
						.collect(toList());
				//@formatter:on
				logger.trace("Adding files:");
				logger.trace(pathStringList.toString());
				if (CollectionUtils.isNotEmpty(pathStringList)) {
					settings.addFiles(pathStringList);
					settings.setCurrentDir(new File(pathStringList.get(0)).getParent());
				}
				selectionMade = true;
			}
		} else if (settings.getCurrentMode() == Settings.MODE_SINGLE_DIR) {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (settings.getCurrentDir() != null) {
				directoryChooser.setInitialDirectory(new File(settings.getCurrentDir()));
			}
			File file = directoryChooser.showDialog(parentWindow);
			if (file != null) {
				settings.setCurrentDir(file.getAbsolutePath());
				selectionMade = true;
			}
		}

		if (selectionMade) {
			saveButton.setDisable(false);
		}
	}
}
