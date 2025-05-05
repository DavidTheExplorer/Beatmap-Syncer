package dte.beatmapsyncer.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class LoggerExceptionHandler implements IExecutionExceptionHandler
{
    private static final Logger LOGGER = LogManager.getLogger("LoggerExceptionHandler");

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
        LOGGER.error("Exception while copying \"{}\"", exception.getSongFolder().getName(), exception);
    }

    private void handle(Exception exception)
    {
        LOGGER.error("Unexpected Exception was encountered", exception);
    }
}
