package com.ihatebrooms.wallpaper.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.ext.javafx.beans.value.SimpleStringPropertyExt;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings implements Serializable {

	private static final Logger logger = LogManager.getLogger(Settings.class);

	// TODO: convert to enums or other best practice

	public final static int MODE_SINGLE_FILE = 0;
	public final static int MODE_MULTI_FILE = 1;
	public final static int MODE_SINGLE_DIR = 2;

	public final static int MODE_DELAY_SECONDS = 0;
	public final static int MODE_DELAY_MINUTES = 1;
	public final static int MODE_DELAY_HOURS = 2;

	protected String currentWallpaper;
	protected SimpleStringPropertyExt filePath = new SimpleStringPropertyExt();
	protected String currentDir;
	protected int currentMode;
	protected int changeDelay = 0;
	protected int changeDelayMode;
	protected int listIdx = 0;
	protected boolean recurseSubDirs;
	protected boolean randomizeList;
	protected boolean allowDuplicates;
	protected List<String> fileList = new LinkedList<>();
	protected transient ObservableList<String> observedList = FXCollections.observableList(fileList);

	public void addFiles(Collection<String> incFiles) {
		for (String inc : incFiles) {
			observedList.add(inc);
		}
	}

	public void resetListIdx() {
		this.listIdx = -1;
	}
	public void setFilePath(String s) {
		this.filePath.set(s);
	}

	public void setCurrentMode(int i) {
		this.currentMode = i;
		this.resetListIdx();
		this.fileList.clear();
	}

	public int getCalcDelay() {
		int delay = this.changeDelay;
		switch (this.changeDelayMode) {
			case MODE_DELAY_HOURS :
				delay *= 60;
			case MODE_DELAY_MINUTES :
				delay *= 60;
			case MODE_DELAY_SECONDS :
				delay *= 1000;
				break;
		}
		return delay;
	}

	public void copyFrom(Settings copyFrom) {
		this.currentWallpaper = copyFrom.currentWallpaper;
		this.filePath.set(copyFrom.getFilePath().get());
		this.currentDir = copyFrom.currentDir;
		this.currentMode = copyFrom.currentMode;
		this.changeDelay = copyFrom.changeDelay;
		this.changeDelayMode = copyFrom.changeDelayMode;
		this.listIdx = copyFrom.listIdx;
		this.recurseSubDirs = copyFrom.recurseSubDirs;
		this.randomizeList = copyFrom.randomizeList;
		this.allowDuplicates = copyFrom.allowDuplicates;
		this.fileList = new LinkedList<>(copyFrom.fileList);
		this.observedList = FXCollections.observableList(fileList);
	}
}
