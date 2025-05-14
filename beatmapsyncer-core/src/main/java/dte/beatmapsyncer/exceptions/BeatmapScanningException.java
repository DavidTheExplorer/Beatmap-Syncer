package dte.beatmapsyncer.exceptions;

import java.io.Serial;

public class BeatmapScanningException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = -7153954851134684027L;

    public BeatmapScanningException(Throwable cause)
    {
        super(cause);
    }
}
