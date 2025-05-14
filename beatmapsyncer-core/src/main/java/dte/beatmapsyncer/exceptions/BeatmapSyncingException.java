package dte.beatmapsyncer.exceptions;

import dte.beatmapsyncer.beatmap.Beatmap;

import java.io.Serial;
import java.util.Optional;

public class BeatmapSyncingException extends RuntimeException
{
	private final Beatmap beatmap;
	
	@Serial
	private static final long serialVersionUID = -3669683519692752337L;

	public BeatmapSyncingException(Throwable cause)
	{
		this(cause, null);
	}

	public BeatmapSyncingException(Throwable cause, Beatmap beatmap)
	{
		super(cause);

		this.beatmap = beatmap;
	}

	public Optional<Beatmap> getBeatmap()
	{
		return Optional.ofNullable(this.beatmap);
	}
}
