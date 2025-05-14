package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.cli.exceptions.LoggerExceptionHandler;
import dte.beatmapsyncer.cli.exceptions.SimpleParameterExceptionHandler;
import dte.beatmapsyncer.utils.OperatingSystem;
import dte.beatmapsyncer.utils.OSUtils;
import picocli.CommandLine;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.IDefaultValueProviderBuilder;

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
                .setDefaultValueProvider(createDefaultValueProvider())
                .setExecutionExceptionHandler(new LoggerExceptionHandler())
                .setParameterExceptionHandler(new SimpleParameterExceptionHandler())
                .execute(args);

        System.exit(exitCode);
    }

    private static IDefaultValueProvider createDefaultValueProvider()
    {
        return new IDefaultValueProviderBuilder()
                .forOption("-gameFolder", OSUtils.getGameFolder().toString())
                .build();
    }
}
