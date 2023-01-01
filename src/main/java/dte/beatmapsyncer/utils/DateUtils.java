package dte.beatmapsyncer.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils 
{
	public static LocalDateTime getLastModified(File file) throws IOException
	{
		return Files.readAttributes(file.toPath(), BasicFileAttributes.class).lastModifiedTime()
				.toInstant()
				.atZone(ZoneId.systemDefault())
				.toLocalDateTime();
	}
}