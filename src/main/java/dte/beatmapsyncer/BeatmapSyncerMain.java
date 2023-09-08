package dte.beatmapsyncer;

import static dte.beatmapsyncer.utils.StringUtils.repeat;
import static dte.beatmapsyncer.utils.UncheckedExceptions.uncheckedTest;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dte.beatmapsyncer.cli.BeatmapSyncerDefaultProvider;
import dte.beatmapsyncer.exceptions.SongSyncingException;
import dte.beatmapsyncer.utils.DateUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "beatmapsyncer",
description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.", 
defaultValueProvider = BeatmapSyncerDefaultProvider.class)
public class BeatmapSyncerMain implements Runnable
{
	@Option(names = "-gameFolder")
	private File gameFolder;
	private File dataFolder, songsFolder;
	private LocalDateTime lastSyncDate;

	private static final Logger LOGGER = LogManager.getLogger("BeatmapSyncer");
	private static final DateTimeFormatter SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss");

	@Override
	public void run()
	{
		this.dataFolder = getDataFolder();
		this.songsFolder = new File(this.gameFolder, "Songs");
		this.lastSyncDate = checkLastSyncDate();

		if(this.lastSyncDate == null)
		{
			generateSyncFolder();
			LOGGER.info("Starting to track beatmap changes from now!");
			return;
		}

		LOGGER.info("Searching unsynchronized songs...");

		List<File> unsyncSongs = searchUnsyncSongs();

		if(unsyncSongs.isEmpty()) 
		{
			LOGGER.info("No unsynchronized songs were found since {}!", SYNC_DATE_FORMATTER.format(this.lastSyncDate));
			return;
		}

		LOGGER.info("Found {}!", unsyncSongs.size());
		
		try 
		{
			sync(unsyncSongs);
		}
		catch(SongSyncingException exception) 
		{
			LOGGER.error("Exception while copying \"{}\"", exception.getSongFolder().getName(), exception);
			return;
		}

		LOGGER.info("Successfully synchronized everything!");
	}

	private List<File> searchUnsyncSongs()
	{
		return Arrays.stream(this.songsFolder.listFiles())
				.filter(File::isDirectory)
				.filter(uncheckedTest(songFolder -> DateUtils.getLastModified(songFolder).isAfter(this.lastSyncDate)))
				.collect(toList());
	}

	private LocalDateTime checkLastSyncDate()
	{
		return Arrays.stream(this.dataFolder.listFiles())
				.map(File::getName)
				.map(fileName -> LocalDateTime.parse(fileName, SYNC_DATE_FORMATTER))
				.max(naturalOrder())
				.orElse(null);
	}

	private void sync(List<File> unsyncSongs) throws SongSyncingException
	{
		File syncFolder = generateSyncFolder();

		int longestSongName = unsyncSongs.stream()
				.map(file -> file.getName().length())
				.max(naturalOrder())
				.get();

		String separator = repeat("-", 18 + longestSongName + String.valueOf(unsyncSongs.size()).length());

		LOGGER.info(separator);

		for(int i = 0; i < unsyncSongs.size(); i++) 
		{
			File songFolder = unsyncSongs.get(i);

			LOGGER.info("Syncing \"{}\"{}({}/{})", 
					songFolder.getName(),
					repeat(" ", longestSongName - songFolder.getName().length() + 5),
					i+1,
					unsyncSongs.size());
			
			try 
			{
				FileUtils.copyDirectoryToDirectory(songFolder, syncFolder);
			}
			catch(Exception exception) 
			{
				throw new SongSyncingException(songFolder);
			}
		}

		LOGGER.info(separator);
	}

	private File getDataFolder() 
	{
		File folder = new File(this.gameFolder, "Beatmap Syncer");

		if(!folder.exists())
			folder.mkdir();

		return folder;
	}

	private File generateSyncFolder() 
	{
		File folder = new File(this.dataFolder, LocalDateTime.now().format(SYNC_DATE_FORMATTER));

		if(!folder.exists())
			folder.mkdir();

		return folder;
	}

	public static void main(String[] args) 
	{
		System.exit(new CommandLine(new BeatmapSyncerMain()).execute(args));
	}
}