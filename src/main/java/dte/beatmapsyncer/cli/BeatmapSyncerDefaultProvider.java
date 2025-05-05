package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.utils.OSUtils;
import dte.beatmapsyncer.utils.picoli.AbstractDefaultValueProvider;

public class BeatmapSyncerDefaultProvider extends AbstractDefaultValueProvider
{
	public BeatmapSyncerDefaultProvider() 
	{
		forOption("-gameFolder", () -> OSUtils.getGameFolder().toString());
	}
}
