package com.ihatebrooms.wallpaper.javafx.scene.image;

import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewExt extends ImageView {

	private static final Logger logger = LogManager.getLogger(ImageViewExt.class);

	public boolean setImage(String filePath) {
		boolean imageChanged = true;

		if (StringUtils.isEmpty(filePath)) {
			imageChanged = false;
		} else {

			try {
				Image image = null;
				image = new Image(new FileInputStream(filePath));
				this.setImage(image);
			} catch (Exception e) {
				imageChanged = false;
				logger.error(e.getMessage());
			}
		}
		return imageChanged;
	}
}