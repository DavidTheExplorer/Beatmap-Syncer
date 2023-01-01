package dte.beatmapsyncer.utils;

public class StringUtils 
{
	public static String repeat(String text, int times) 
	{
		StringBuilder builder = new StringBuilder(text.length() * times);
		
		for(int i = 1; i <= times; i++)
			builder.append(text);
		
		return builder.toString();
	}
}
