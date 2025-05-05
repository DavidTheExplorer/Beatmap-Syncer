package dte.beatmapsyncer.exceptions;

import java.io.Serial;
import java.nio.file.Path;

public class SongSyncingException extends RuntimeException
{
	private final Path songFolder;
	
	@Serial
	private static final long serialVersionUID = -3669683519692752337L;

	public SongSyncingException(Path songFolder)
	{
		this.songFolder = songFolder;
	}

	public Path getSongFolder()
	{
		return this.songFolder;
	}
}
