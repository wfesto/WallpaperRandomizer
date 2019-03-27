package com.ihatebrooms.wallpaper.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Settings extends Observable implements Serializable {

	private static final Logger logger = LogManager.getLogger(Settings.class);

	// TODO: convert to enums or other best practice

	public final static int MODE_SINGLE_FILE = 0;
	public final static int MODE_MULTI_FILE = 1;
	public final static int MODE_SINGLE_DIR = 2;

	public final static int MODE_DELAY_SECONDS = 0;
	public final static int MODE_DELAY_MINUTES = 1;
	public final static int MODE_DELAY_HOURS = 2;

	protected String currentWallpaper;
	protected String filePath;
	protected String currentDir;
	protected int currentMode;
	protected int changeDelay = 0;
	protected int changeDelayMode;
	protected int listIdx = 0;
	protected boolean recurseSubDirs;
	protected boolean randomizeList;
	protected List<String> fileList;

	public Settings(Settings copyFrom) {
		this.copyFrom(copyFrom);
	}

	public void addFiles(List<String> newList) {
		this.getFileList().addAll(newList);
	}

	public void setFilePath(String s) {
		this.filePath = s;
		this.setChanged();
		this.notifyObservers();
	}

	public void resetListIdx() {
		this.listIdx = -1;
	}

	public void setCurrentMode(int i) {
		this.currentMode = i;
		this.resetListIdx();
		this.setFileList(null);
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

	public List<String> getFileList() {
		return (this.fileList = (this.fileList == null ? new LinkedList<String>() : this.fileList));
	}

	public void copyFrom(Settings copyFrom) {
		this.currentWallpaper = copyFrom.currentWallpaper;
		this.setFilePath(copyFrom.filePath);
		this.currentDir = copyFrom.currentDir;
		this.currentMode = copyFrom.currentMode;
		this.changeDelay = copyFrom.changeDelay;
		this.changeDelayMode = copyFrom.changeDelayMode;
		this.listIdx = copyFrom.listIdx;
		this.recurseSubDirs = copyFrom.recurseSubDirs;
		this.randomizeList = copyFrom.randomizeList;
		this.fileList = new LinkedList<>();
		this.fileList.addAll(copyFrom.getFileList());
	}
}
