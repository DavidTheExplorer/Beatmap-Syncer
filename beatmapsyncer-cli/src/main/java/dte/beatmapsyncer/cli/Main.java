package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.cli.exceptions.LoggerExceptionHandler;
import dte.beatmapsyncer.cli.exceptions.SimpleParameterExceptionHandler;
import dte.beatmapsyncer.utils.OperatingSystem;
import picocli.CommandLine;

public class Main
{
    public static void main(String[] args)
    {
        if(OperatingSystem.detectCurrent() != OperatingSystem.WINDOWS)
        {
            System.err.println("Error: BeatmapSyncer is currently supported only on Windows.");
            return;
        }

        int exitCode = new CommandLine(new BeatmapSyncerCLI())
                .setDefaultValueProvider(new BeatmapSyncerDefaultValueProvider())
                .setExecutionExceptionHandler(new LoggerExceptionHandler())
                .setParameterExceptionHandler(new SimpleParameterExceptionHandler())
                .execute(args);

        System.exit(exitCode);
    }
}
