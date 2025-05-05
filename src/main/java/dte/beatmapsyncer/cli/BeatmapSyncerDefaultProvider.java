package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.utils.OSUtils;
import picocli.CommandLine.IDefaultValueProvider;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;

public class BeatmapSyncerDefaultProvider implements IDefaultValueProvider
{
	@Override
	public String defaultValue(ArgSpec argSpec) throws Exception
	{
		if(!argSpec.isOption())
			return null;

		OptionSpec optionSpec = (OptionSpec) argSpec;

		return switch(optionSpec.longestName())
		{
			case "-gameFolder" -> OSUtils.getGameFolder().toString();

            default -> null;
        };
	}
}
