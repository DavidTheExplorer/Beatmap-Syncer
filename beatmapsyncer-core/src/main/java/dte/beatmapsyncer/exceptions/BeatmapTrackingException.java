package dte.beatmapsyncer.exceptions;

import java.io.Serial;

public class BeatmapTrackingException extends RuntimeException
{
    @Serial
    private static final long serialVersionUID = 4462298542422434498L;

    public BeatmapTrackingException(Throwable cause)
    {
        super(cause);
    }
}
