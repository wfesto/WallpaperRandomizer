package com.ihatebrooms.wallpaper.application.ui.controls;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.application.ui.WallpaperUIElements;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileListButtonController implements EventHandler<ActionEvent> {

	private static final Logger logger = LogManager.getLogger(FileListButtonController.class);

	protected WallpaperUIElements ui;

	@Override
	public void handle(ActionEvent arg0) {
		List<Integer> idxList = ui.fileListView.getSelectionModel().getSelectedIndices();

		logger.trace("Selected idxs:" + idxList);

		if (arg0.getSource() == ui.deleteListEntryButton) {
			for (Integer idx : idxList) {
				logger.trace("Removing element at index: " + idx);
				ui.fileListView.getItems().remove(idx.intValue());
			}
		} else if (arg0.getSource() == ui.listUpButton) {

		} else if (arg0.getSource() == ui.listDownButton) {

		}

		ui.saveButton.setDisable(false);
		ui.fileListView.refresh();
	}

}
