package dte.beatmapsyncer.exceptions;

import dte.beatmapsyncer.beatmap.Beatmap;

import java.io.Serial;

public class BeatmapSyncingException extends RuntimeException
{
	private final Beatmap beatmap;
	
	@Serial
	private static final long serialVersionUID = -3669683519692752337L;

	public BeatmapSyncingException(Throwable cause, Beatmap beatmap)
	{
		super(cause);

		this.beatmap = beatmap;
	}

	public Beatmap getBeatmap()
	{
		return this.beatmap;
	}
}
