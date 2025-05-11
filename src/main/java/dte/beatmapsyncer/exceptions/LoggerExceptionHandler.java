package dte.beatmapsyncer.exceptions;

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
        if(exception instanceof SongSyncingException syncingException)
            handle(syncingException);

        else
            handle(exception);

        return commandLine.getCommandSpec().exitCodeOnExecutionException();
    }

    private void handle(SongSyncingException exception)
    {
        LOGGER.error("Error while syncing beatmap \"{}\"", exception.getSongFolder().getFileName(), exception.getCause());
    }

    private void handle(Exception exception)
    {
        LOGGER.error("Unexpected Error was encountered", exception);
    }
}
