package dte.beatmapsyncer;

import static dte.beatmapsyncer.utils.StringUtils.repeat;
import static dte.beatmapsyncer.utils.UncheckedExceptions.uncheckedTest;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import dte.beatmapsyncer.cli.BeatmapSyncerDefaultProvider;
import dte.beatmapsyncer.utils.DateUtils;
import dte.beatmapsyncer.utils.LoggerUtils;
import dte.beatmapsyncer.utils.StringSubstitutor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "beatmapsyncer", description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.",  defaultValueProvider = BeatmapSyncerDefaultProvider.class)
public class BeatmapSyncer implements Runnable
{
	@Option(names = "-gameFolder")
	private File gameFolder;

	private File dataFolder, songsFolder;
	private LocalDateTime lastSyncDate;

	private static final Logger LOGGER = LoggerUtils.newConsoleLogger(BeatmapSyncer.class.getSimpleName());
	private static final DateTimeFormatter SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss");

	@Override
	public void run()
	{
		this.dataFolder = getDataFolder();
		this.songsFolder = new File(this.gameFolder, "Songs");
		this.lastSyncDate = getLastSyncDate();

		if(this.lastSyncDate == null)
		{
			generateSyncFolder();
			LOGGER.info("Starting to track beatmap changes from now!");
			return;
		}

		LOGGER.info("Searching for unsynchronized songs...");

		List<File> unsyncedSongs = getUnsyncedSongs();

		if(unsyncedSongs.isEmpty()) 
		{
			LOGGER.info(String.format("No unsynchronized songs were found since %s!", SYNC_DATE_FORMATTER.format(this.lastSyncDate)));
			return;
		}

		LOGGER.info(String.format("Found %d!", unsyncedSongs.size()));
		sync(unsyncedSongs);
		LOGGER.info("Successfully synchronized everything!");
	}

	private List<File> getUnsyncedSongs()
	{
		return Arrays.stream(this.songsFolder.listFiles())
				.filter(File::isDirectory)
				.filter(uncheckedTest(songFolder -> DateUtils.getLastModified(songFolder).isAfter(this.lastSyncDate)))
				.collect(toList());
	}

	private LocalDateTime getLastSyncDate()
	{
		return Arrays.stream(this.dataFolder.listFiles())
				.map(File::getName)
				.map(fileName -> LocalDateTime.parse(fileName, SYNC_DATE_FORMATTER))
				.max(naturalOrder())
				.orElse(null);
	}

	private void sync(List<File> unsyncedSongs)
	{
		File syncFolder = generateSyncFolder();

		int longestSongName = unsyncedSongs.stream()
				.map(file -> file.getName().length())
				.max(naturalOrder())
				.get();
		
		String separator = repeat("-", 18 + longestSongName + String.valueOf(unsyncedSongs.size()).length());
		
		LOGGER.info(separator);
		
		for(int i = 0; i < unsyncedSongs.size(); i++) 
		{
			File songFolder = unsyncedSongs.get(i);

			LOGGER.info(new StringSubstitutor("Syncing \"${song}\"${spaces}(${index}/${total songs})")
					.inject("song", songFolder.getName())
					.inject("spaces", repeat(" ", longestSongName - songFolder.getName().length() + 5))
					.inject("index", i+1)
					.inject("total songs", unsyncedSongs.size())
					.apply());

			try 
			{
				FileUtils.copyDirectoryToDirectory(songFolder, syncFolder);
			} 
			catch (IOException exception) 
			{
				LOGGER.severe("Exception while copying '%s': %s".formatted(songFolder, exception.getMessage()));
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
		System.exit(new CommandLine(new BeatmapSyncer()).execute(args));
	}
}