package com.ihatebrooms.wallpaper.ext.javafx.scene.image;

import java.io.FileInputStream;
import java.util.Observable;
import java.util.Observer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ihatebrooms.wallpaper.data.Settings;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewExt extends ImageView implements Observer {

	private static final Logger logger = LogManager.getLogger(ImageViewExt.class);

	public boolean setImage(String filePath) {
		logger.trace("Showing image: " + filePath);
		boolean imageChanged = false;
		if (!StringUtils.isEmpty(filePath)) {
			try {
				this.setImage(new Image(new FileInputStream(filePath)));
				imageChanged = true;
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		return imageChanged;
	}

	@Override
	public void update(Observable observedObj, Object updateArg) {
		Settings settings = (Settings) observedObj;
		this.setImage(settings.getFilePath());
	}
}