package com.ihatebrooms.wallpaper.application;

import java.util.HashMap;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

public class WallpaperUpdater {
	public static void updateWallpaper(String path) {
		// from MSDN article
		long SPI_SETDESKWALLPAPER = 20;
		long SPIF_UPDATEINIFILE = 0x01;
		long SPIF_SENDWININICHANGE = 0x02;

		Runtime.getRuntime().loadLibrary("user32");
		SPI.INSTANCE.SystemParametersInfo(SPI_SETDESKWALLPAPER, 0, path, SPIF_UPDATEINIFILE | SPIF_SENDWININICHANGE);
	}

	public interface SPI extends StdCallLibrary {
		SPI INSTANCE = Native.load("user32", SPI.class, new HashMap<String, Object>() {
			private static final long serialVersionUID = 4317313094931233989L;

			{
				put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
				put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
			}
		});

		boolean SystemParametersInfo(long uiAction, long uiParam, String pvParam, long fWinIni);
	}
}
