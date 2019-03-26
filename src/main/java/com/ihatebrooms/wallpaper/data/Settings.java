package com.ihatebrooms.wallpaper.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Observable implements Serializable, Cloneable {

	private static final Logger logger = LogManager.getLogger(Settings.class);

	public final static int MODE_SINGLE_FILE = 0;
	public final static int MODE_MULTI_FILE = 1;
	public final static int MODE_SINGLE_DIR = 2;

	protected String currentWallpaper;
	protected String filePath;
	protected String currentDir;
	protected int currentMode;
	protected int changeDelay;
	protected int listIdx = 0;
	protected boolean recurseSubDirs;
	protected boolean randomizeList;
	protected List<String> fileList;

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
		this.listIdx = -1;
		this.setFileList(null);
	}

	public List<String> getFileList() {
		return (this.fileList = (this.fileList == null ? new LinkedList<String>() : this.fileList));

	}

	public Object clone() {
		Settings newSettings = new Settings();
		try {
			newSettings = (Settings) super.clone();
			newSettings.setFileList(new LinkedList<>());
			newSettings.getFileList().addAll(this.getFileList());
		} catch (CloneNotSupportedException e) {
			logger.error("Error cloning settings:");
			logger.error(e.getMessage());
		}

		return newSettings;
	}
}
