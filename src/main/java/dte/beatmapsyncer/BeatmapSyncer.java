package dte.beatmapsyncer;

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

import dte.beatmapsyncer.utils.DateUtils;
import dte.beatmapsyncer.utils.OSUtils;
import dte.beatmapsyncer.utils.StringSubstitutor;
import dte.beatmapsyncer.utils.StringUtils;

public class BeatmapSyncer
{
	private static final DateTimeFormatter SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss");

	private static final Logger LOGGER = Logger.getLogger(BeatmapSyncer.class.getSimpleName());

	public static void main(String[] args) throws IOException
	{
		File gameFolder = parseGameFolder(args);
		File dataFolder = getDataFolder(gameFolder);
		File songsFolder = new File(gameFolder, "Songs");

		LocalDateTime lastSyncDate = getLastSyncDate(dataFolder);

		if(lastSyncDate == null)
		{
			generateSyncFolder(dataFolder);
			LOGGER.info("Starting to track beatmap changes from now!");
			return;
		}

		List<File> unsyncedSongs = getUnsyncedSongs(songsFolder, lastSyncDate);

		if(unsyncedSongs.isEmpty()) 
		{
			LOGGER.info("No unsync songs were found!");
			return;
		}

		LOGGER.info(String.format("Syncing %d songs...", unsyncedSongs.size()));
		sync(unsyncedSongs, dataFolder);
		LOGGER.info("Success!");
	}

	private static File parseGameFolder(String[] args) 
	{
		return (args.length == 0) ? OSUtils.getGameFolder() : new File(args[0]);
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
		int longestSongName = unsyncedSongs.stream()
				.map(file -> file.getName().length())
				.max(naturalOrder())
				.get();
		
		File syncFolder = generateSyncFolder(dataFolder);
		
		for(int i = 0; i < unsyncedSongs.size(); i++) 
		{
			File songFolder = unsyncedSongs.get(i);

			LOGGER.info(new StringSubstitutor("Syncing \"${song}\"${spaces}(${index}/${total songs})")
					.inject("song", songFolder.getName())
					.inject("spaces", StringUtils.repeat(" ", longestSongName - songFolder.getName().length() + 5))
					.inject("index", ++i)
					.inject("total songs", unsyncedSongs.size())
					.apply());

			FileUtils.copyDirectoryToDirectory(songFolder, syncFolder);
		}
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