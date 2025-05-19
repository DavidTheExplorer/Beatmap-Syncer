package dte.beatmapsyncer.cli.exceptions;

import dte.beatmapsyncer.exceptions.BeatmapScanningException;
import dte.beatmapsyncer.exceptions.BeatmapSyncingException;
import dte.beatmapsyncer.exceptions.BeatmapTrackingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class LoggerExceptionHandler implements IExecutionExceptionHandler
{
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerExceptionHandler.class);

    @Override
    public int handleExecutionException(Exception exception, CommandLine commandLine, ParseResult parseResult) throws Exception
    {
        if(exception instanceof BeatmapScanningException scanningException)
            handle(scanningException);

        else if(exception instanceof BeatmapSyncingException syncingException)
            handle(syncingException);

        else if(exception instanceof BeatmapTrackingException trackingException)
            handle(trackingException);

        else
            handle(exception);

        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }

    private void handle(BeatmapScanningException exception)
    {
        LOGGER.error("Error while scanning for unsync beatmaps", exception.getCause());
    }

    private void handle(BeatmapTrackingException exception)
    {
        LOGGER.error("Error while starting to track beatmaps", exception.getCause());
    }

    private void handle(BeatmapSyncingException exception)
    {
        String subject = exception.getBeatmap()
                .map(beatmap -> "beatmap \"%s\"".formatted(beatmap.name()))
                .orElse("beatmaps");

        LOGGER.error("Error while syncing {}", subject, exception.getCause());
    }

    private void handle(Exception exception)
    {
        LOGGER.error("Unexpected Error", exception);
    }
}
