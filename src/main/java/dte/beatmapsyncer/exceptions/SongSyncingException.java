package dte.beatmapsyncer.exceptions;

import java.io.File;
import java.io.Serial;

public class SongSyncingException extends RuntimeException
{
	private final File songFolder;
	
	@Serial
	private static final long serialVersionUID = -3669683519692752337L;

	public SongSyncingException(File songFolder)
	{
		this.songFolder = songFolder;
	}

	public File getSongFolder()
	{
		return this.songFolder;
	}
}
