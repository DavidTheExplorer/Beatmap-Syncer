package dte.beatmapsyncer.cli;

import java.io.File;

import dte.beatmapsyncer.utils.OSUtils;
import lombok.Getter;
import picocli.CommandLine;
import picocli.CommandLine.Option;

@Getter
public class BeatmapSyncerArgs 
{
	@Option(names = "-gameFolder")
	private File gameFolder = OSUtils.getGameFolder();
	
	public static BeatmapSyncerArgs from(String[] args) 
	{
		BeatmapSyncerArgs parsedArgs = new BeatmapSyncerArgs();
		new CommandLine(parsedArgs).parseArgs(args);
		
		return parsedArgs;
	}
}