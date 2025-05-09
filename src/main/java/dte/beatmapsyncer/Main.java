package dte.beatmapsyncer;

import dte.beatmapsyncer.exceptions.LoggerExceptionHandler;
import dte.beatmapsyncer.exceptions.SimpleParameterExceptionHandler;
import dte.beatmapsyncer.utils.OSUtils;
import picocli.CommandLine;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.IDefaultValueProviderBuilder;

public class Main
{
    public static void main(String[] args)
    {
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
