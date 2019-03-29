package com.ihatebrooms.wallpaper.ext.javafx.scene.image;

import java.io.FileInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewExt extends ImageView implements ChangeListener<String> {

	private static final Logger logger = LogManager.getLogger(ImageViewExt.class);

	public boolean setImage(StringProperty stringProperty) {
		return this.setImage(stringProperty.getValue());
	}

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
	public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
		this.setImage(newValue);
	}
}