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

	protected String filePath;
	protected String currentDir;
	protected int currentMode;
	protected boolean recurseSubDirs;
	protected List<String> fileList;

	public void forceUpdate() {
		this.update();
	}

	public List<String> getFileList() {
		return (this.fileList == null ? new LinkedList<String>() : this.fileList);
	}

	public void setFilePath(String s) {
		this.filePath = s;
		this.update();
	}

	public void setCurrentDir(String s) {
		this.currentDir = s;
		this.update();
	}

	public void setCurrentMode(int i) {
		this.currentMode = i;
		this.update();
	}

	public void setRecurseDirs(boolean b) {
		this.recurseSubDirs = b;
		this.update();
	}

	public void setFileList(List<String> sL) {
		this.fileList = sL;
		this.update();
	}

	protected void update() {
		this.setChanged();
		this.notifyObservers();
	}

}
