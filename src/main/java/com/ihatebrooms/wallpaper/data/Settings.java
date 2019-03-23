package com.ihatebrooms.wallpaper.data;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Settings extends Observable implements Serializable {

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

	public void forceUpdate() {
		this.update();
	}

	public void addFiles(List<String> newList) {
		this.getFileList().addAll(newList);
		this.update(newList);
	}

	public void resetListIdx() {
		this.listIdx = -1;
	}

	public void setFilePath(String s) {
		this.filePath = s;
		this.update();
	}

	public void setCurrentMode(int i) {
		this.currentMode = i;
		this.listIdx = -1;
		this.setFileList(null);
		this.update();
	}

	public List<String> getFileList() {
		return (this.fileList = (this.fileList == null ? new LinkedList<String>() : this.fileList));

	}

	protected void update() {
		this.setChanged();
		this.notifyObservers();
	}

	protected void update(Object o) {
		this.setChanged();
		this.notifyObservers(o);
	}
}
