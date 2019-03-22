package com.ihatebrooms.wallpaper.event.handler;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileChoiceEventHandler implements EventHandler<ActionEvent> {

	private static final Logger logger = LogManager.getLogger(FileChoiceEventHandler.class);

	protected Window parentWindow;
	protected Settings settings;
	protected static final String[] imgTypes = {"bmp", "jpeg", "jpg", "png"};

	@Override
	public void handle(ActionEvent arg0) {
		FileChooser fileChooser = new FileChooser();
		if (settings.getCurrentDir() != null) {
			fileChooser.setInitialDirectory(new File(settings.getCurrentDir()));
		}
		ExtensionFilter imageExtensionFilter = new ExtensionFilter("Images", Arrays.stream(imgTypes).map(p -> "*.".concat(p)).collect(toList()));
		fileChooser.getExtensionFilters().add(imageExtensionFilter);

		if (settings.getCurrentMode() == Settings.MODE_SINGLE_FILE) {
			File file = fileChooser.showOpenDialog(parentWindow);
			if (file != null && isImage(file)) {
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
			List<File> fileList = null;
			if (file != null) {
				settings.setCurrentDir(file.getAbsolutePath());
				try {
					int walkDepth = settings.isRecurseSubDirs() ? Integer.MAX_VALUE : 0;
					//@formatter:off
					fileList = Files.walk(Paths.get(settings.getCurrentDir()), walkDepth)
						.map(p -> p.toFile())
						.filter(p -> p.isFile())
						.filter(p -> isImage(p))
						.collect(toList());
					//@formatter:on
					settings.setFilePath(fileList.get(settings.getListIdx()).getAbsolutePath());
					settings.setFileList(fileList.stream().map(p -> p.getAbsolutePath()).collect(toList()));
					settings.resetListIdx();
					logger.trace("Reading dir: " + settings.getCurrentDir());
					logger.trace("Files found: " + fileList.toString());
				} catch (IOException e) {
					logger.error("Unable to iterate directory:");
					logger.error(e.getMessage());
				}

			}
		}
	}

	protected boolean isImage(File f) {
		boolean found = false;
		for (int i = 0; i < imgTypes.length && !found; ++i) {
			found = f.getName().toLowerCase().endsWith(imgTypes[i].toLowerCase());
		}
		return found;
	}

}
