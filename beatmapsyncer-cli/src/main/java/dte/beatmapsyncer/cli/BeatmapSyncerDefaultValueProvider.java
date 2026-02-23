package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.utils.OSUtils;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;

public class BeatmapSyncerDefaultValueProvider implements IDefaultValueProvider
{
    @Override
    public String defaultValue(ArgSpec argSpec) throws Exception
    {
        if(!argSpec.isOption())
            return null;

        String optionName = ((OptionSpec) argSpec).longestName();

        return switch(optionName)
        {
            case "--gameFolder" -> OSUtils.getGameFolder().toString();
            default -> null;
        };
    }
}
