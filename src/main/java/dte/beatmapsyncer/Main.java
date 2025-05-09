package dte.beatmapsyncer;

import dte.beatmapsyncer.cli.BeatmapSyncerDefaultProvider;
import dte.beatmapsyncer.exceptions.LoggerExceptionHandler;
import picocli.CommandLine;

public class Main
{
    public static void main(String[] args)
    {
        System.exit(new CommandLine(new BeatmapSyncer())
                .setDefaultValueProvider(new BeatmapSyncerDefaultProvider())
                .setExecutionExceptionHandler(new LoggerExceptionHandler())
                .execute(args));
    }
}
