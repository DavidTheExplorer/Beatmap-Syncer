package dte.beatmapsyncer.utils;

import java.io.File;

public class OSUtils 
{
	public static File getSongsFolder()
	{
		String windowsUser = System.getProperty("user.name");
		
		return new File(String.format("C:\\Users\\%s\\AppData\\Local\\osu!\\Songs", windowsUser));
	}
}