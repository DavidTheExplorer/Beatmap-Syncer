package dte.beatmapsyncer.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils 
{
	public static LocalDateTime getLastModified(Path path)
	{
        try
		{
            return Files.getLastModifiedTime(path)
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
		catch(IOException exception)
		{
            throw new UncheckedIOException(exception);
        }
    }
}