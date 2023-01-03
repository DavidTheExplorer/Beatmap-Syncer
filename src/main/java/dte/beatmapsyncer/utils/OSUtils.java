package dte.beatmapsyncer.utils;

import java.io.File;

public class OSUtils
{
	private static final String WINDOWS_USERNAME = System.getProperty("user.name");
	
	public static File getGameFolder()
	{
		return new File(String.format("C:\\Users\\%s\\AppData\\Local\\osu!", WINDOWS_USERNAME));
	}
}