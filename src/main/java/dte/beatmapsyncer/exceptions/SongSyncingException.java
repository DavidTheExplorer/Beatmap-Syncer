package dte.beatmapsyncer.exceptions;

import java.io.File;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SongSyncingException extends RuntimeException
{
	private final File songFolder;
	
	private static final long serialVersionUID = -3669683519692752337L;
}
