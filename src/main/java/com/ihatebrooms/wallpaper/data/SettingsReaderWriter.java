package com.ihatebrooms.wallpaper.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsReaderWriter {

	private static final Logger logger = LogManager.getLogger(SettingsReaderWriter.class);

	private static final String path = "WRSettings.ini";

	private static String activeProfile;
	private static Map<String, Settings> settingsMap;

	@SuppressWarnings("unchecked")
	public static String getActiveProfile() throws Exception {
		if (activeProfile == null || settingsMap == null) {
			if (Files.exists(Paths.get(path))) {
				ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
				activeProfile = (String) inputStream.readObject();
				try {
					settingsMap = (Map<String, Settings>) inputStream.readObject();
				} catch (InvalidClassException e) {
					logger.error(e.getMessage());
					logger.error("Settings file changed, reverting to defaults");
				} finally {
					inputStream.close();
				}
			}

			if (settingsMap == null) {
				settingsMap = new HashMap<>();
				Settings settings = new Settings();
				settingsMap.put("Default", settings);
				activeProfile = "Default";
			}
		}

		return activeProfile;
	}

	public static Map<String, Settings> readSettings() throws Exception {
		getActiveProfile();
		return settingsMap;
	}

	public static void writeSettings(String activeProfile, Map<String, Settings> settingsMap) throws Exception {
		Files.deleteIfExists(Paths.get(path));
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
		outputStream.writeObject(activeProfile);
		outputStream.writeObject(settingsMap);
		outputStream.close();
	}
}
