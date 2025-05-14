package dte.beatmapsyncer.utils;

import java.nio.file.Path;

public class OSUtils
{
	private static final String WINDOWS_USERNAME = System.getProperty("user.name");
	
	public static Path getGameFolder()
	{
		return Path.of("C:", "Users", WINDOWS_USERNAME, "AppData", "Local", "osu!");
	}
}