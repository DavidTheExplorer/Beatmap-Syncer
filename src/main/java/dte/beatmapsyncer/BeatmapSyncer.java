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

import dte.beatmapsyncer.cli.BeatmapSyncerArgs;
import dte.beatmapsyncer.utils.DateUtils;
import dte.beatmapsyncer.utils.LoggerUtils;
import dte.beatmapsyncer.utils.StringSubstitutor;

public class BeatmapSyncer
{
	private static final DateTimeFormatter SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss");

	private static final Logger LOGGER = LoggerUtils.newConsoleLogger(BeatmapSyncer.class.getSimpleName());

	public static void main(String[] args) throws IOException
	{
		BeatmapSyncerArgs parsedArgs = BeatmapSyncerArgs.from(args);
		File gameFolder = parsedArgs.getGameFolder();
		File dataFolder = getDataFolder(gameFolder);
		File songsFolder = new File(gameFolder, "Songs");

		LocalDateTime lastSyncDate = getLastSyncDate(dataFolder);

		if(lastSyncDate == null)
		{
			generateSyncFolder(dataFolder);
			LOGGER.info("Starting to track beatmap changes from now!");
			return;
		}

		LOGGER.info("Searching for unsynchronized songs...");

		List<File> unsyncedSongs = getUnsyncedSongs(songsFolder, lastSyncDate);

		if(unsyncedSongs.isEmpty()) 
		{
			LOGGER.info(String.format("No unsynchronized songs were found since %s!", SYNC_DATE_FORMATTER.format(lastSyncDate)));
			return;
		}

		LOGGER.info(String.format("Found %d!", unsyncedSongs.size()));
		sync(unsyncedSongs, dataFolder);
		LOGGER.info("Successfully synchronized everything!");
	}

	private static List<File> getUnsyncedSongs(File songsFolder, LocalDateTime lastSyncDate)
	{
		return Arrays.stream(songsFolder.listFiles())
				.filter(File::isDirectory)
				.filter(uncheckedTest(songFolder -> DateUtils.getLastModified(songFolder).isAfter(lastSyncDate)))
				.collect(toList());
	}

	private static LocalDateTime getLastSyncDate(File dataFolder) throws IOException 
	{
		return Arrays.stream(dataFolder.listFiles())
				.map(File::getName)
				.map(fileName -> LocalDateTime.parse(fileName, SYNC_DATE_FORMATTER))
				.max(naturalOrder())
				.orElse(null);
	}

	private static void sync(List<File> unsyncedSongs, File dataFolder) throws IOException
	{
		File syncFolder = generateSyncFolder(dataFolder);
		
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

			FileUtils.copyDirectoryToDirectory(songFolder, syncFolder);
		}
		
		LOGGER.info(separator);
	}

	private static File getDataFolder(File gameFolder) 
	{
		File folder = new File(gameFolder, "Beatmap Syncer");

		if(!folder.exists())
			folder.mkdir();

		return folder;
	}

	private static File generateSyncFolder(File dataFolder) 
	{
		File folder = new File(dataFolder, LocalDateTime.now().format(SYNC_DATE_FORMATTER));

		if(!folder.exists())
			folder.mkdir();

		return folder;
	}
}