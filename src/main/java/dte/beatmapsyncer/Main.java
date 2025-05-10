package dte.beatmapsyncer;

import dte.beatmapsyncer.exceptions.LoggerExceptionHandler;
import dte.beatmapsyncer.exceptions.SimpleParameterExceptionHandler;
import dte.beatmapsyncer.utils.OSUtils;
import dte.beatmapsyncer.utils.OperatingSystem;
import picocli.CommandLine;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.IDefaultValueProviderBuilder;

import static dte.beatmapsyncer.utils.OperatingSystem.WINDOWS;

public class Main
{
    public static void main(String[] args)
    {
        if(OperatingSystem.detectCurrent() != WINDOWS)
        {
            System.err.println("Error: BeatmapSyncer is currently supported only on Windows.");
            return;
        }

        System.exit(new CommandLine(new BeatmapSyncer())
                .setDefaultValueProvider(createDefaultValueProvider())
                .setExecutionExceptionHandler(new LoggerExceptionHandler())
                .setParameterExceptionHandler(new SimpleParameterExceptionHandler())
                .execute(args));
    }

    private static IDefaultValueProvider createDefaultValueProvider()
    {
        return new IDefaultValueProviderBuilder()
                .forOption("-gameFolder", OSUtils.getGameFolder().toString())
                .build();
    }
}
