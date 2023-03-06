package dte.beatmapsyncer;

import static java.util.Comparator.naturalOrder;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dte.beatmapsyncer.cli.BeatmapSyncerDefaultProvider;
import dte.beatmapsyncer.detector.SyncType;
import dte.beatmapsyncer.detector.UnsyncSongsDetector;
import dte.beatmapsyncer.exceptions.SongSyncingException;
import dte.beatmapsyncer.syncer.UnsyncSongsSyncer;
import dte.beatmapsyncer.utils.SyncUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "beatmapsyncer",
description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.", 
defaultValueProvider = BeatmapSyncerDefaultProvider.class)
public class BeatmapSyncerApp implements Runnable
{
	@Option(names = "-gameFolder")
	private File gameFolder;
	private File dataFolder, songsFolder;
	private LocalDateTime lastSyncDate;

	private static final Logger LOGGER = LogManager.getLogger(BeatmapSyncerApp.class);

	@Override
	public void run()
	{
		this.dataFolder = getDataFolder();
		this.songsFolder = new File(this.gameFolder, "Songs");
		this.lastSyncDate = checkLastSyncDate();

		if(this.lastSyncDate == null)
		{
			SyncUtils.generateSyncFolder(this.dataFolder);
			LOGGER.info("Starting to track beatmap changes from now!");
			return;
		}

		LOGGER.info("Searching for unsynchronized songs...");

		List<File> unsyncedSongs = new UnsyncSongsDetector(this.songsFolder)
				.searchAfter(this.lastSyncDate)
				.get(SyncType.CHANGED);

		if(unsyncedSongs.isEmpty()) 
		{
			LOGGER.info("No unsynchronized songs were found since {}!", SyncUtils.SYNC_DATE_FORMATTER.format(this.lastSyncDate));
			return;
		}

		LOGGER.info("Found {}!", unsyncedSongs.size());
		
		try 
		{
			new UnsyncSongsSyncer.Builder()
			.forSongs(unsyncedSongs)
			.to(this.dataFolder)
			.build()
			.sync();
		}
		catch(SongSyncingException exception) 
		{
			LOGGER.error("Exception while copying \"{}\"", exception.getSongFolder().getName(), exception);
			return;
		}

		LOGGER.info("Successfully synchronized everything!");
	}

	private LocalDateTime checkLastSyncDate()
	{
		return Arrays.stream(this.dataFolder.listFiles())
				.map(File::getName)
				.map(fileName -> LocalDateTime.parse(fileName, SyncUtils.SYNC_DATE_FORMATTER))
				.max(naturalOrder())
				.orElse(null);
	}
	
	private File getDataFolder() 
	{
		File folder = new File(this.gameFolder, "Beatmap Syncer");

		if(!folder.exists())
			folder.mkdir();

		return folder;
	}

	public static void main(String[] args) 
	{
		System.exit(new CommandLine(new BeatmapSyncerApp()).execute(args));
	}
}