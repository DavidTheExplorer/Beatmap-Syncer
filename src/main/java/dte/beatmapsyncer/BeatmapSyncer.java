package dte.beatmapsyncer;

import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.toList;
import static picocli.CommandLine.ExitCode.OK;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;

import dte.beatmapsyncer.exceptions.SongSyncingException;
import dte.beatmapsyncer.utils.DateUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

@Command(name = "beatmapsyncer", description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.")
public class BeatmapSyncer implements Callable<Integer>
{
	private Path gameFolder, dataFolder;
	private LocalDateTime lastSyncDate;

	@Spec
	private CommandSpec commandSpec;

	public static final DateTimeFormatter
			SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss"),
			SYNC_DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("'on' dd-MM-yyyy 'at' HH:mm:ss");

	@Override
	public Integer call() throws Exception
	{
		this.dataFolder = getDataFolder();
		this.lastSyncDate = checkLastSyncDate();

		if(this.lastSyncDate == null)
		{
			generateSyncFolder();
			System.out.println("Started tracking beatmap changes!");
			return OK;
		}

		System.out.printf("Searching unsync songs... (Last sync was %s)%n", SYNC_DATE_DISPLAY_FORMATTER.format(this.lastSyncDate));

		List<Path> unsyncSongs = searchUnsyncSongs();

		if(unsyncSongs.isEmpty())
		{
			System.out.println("No songs found!");
			return OK;
		}

		System.out.printf("Found %d!%n", unsyncSongs.size());
		sync(unsyncSongs);
		System.out.println("Successfully synced everything!");
		return OK;
	}

	@Option(names = "-gameFolder")
	public void setGameFolder(Path gameFolder)
	{
		if(!Files.exists(gameFolder))
			throw new ParameterException(this.commandSpec.commandLine(), "The provided osu! folder \"%s\" couldn't be found.".formatted(gameFolder));

		if(!Files.exists(getSongsFolder(gameFolder)))
			throw new ParameterException(this.commandSpec.commandLine(), "The provided osu! folder \"%s\" doesn't have a songs folder.".formatted(gameFolder));

		this.gameFolder = gameFolder;
	}

	private List<Path> searchUnsyncSongs() throws IOException
	{
		try(Stream<Path> stream = Files.list(getSongsFolder(this.gameFolder)))
		{
			return stream
					.filter(Files::isDirectory)
					.filter(songFolder -> DateUtils.getLastModified(songFolder).isAfter(this.lastSyncDate))
					.collect(toList());
		}
	}

	private LocalDateTime checkLastSyncDate() throws IOException
	{
		try(Stream<Path> stream = Files.list(this.dataFolder))
		{
			return stream
					.map(Path::getFileName)
					.map(fileName -> LocalDateTime.parse(fileName.toString(), SYNC_DATE_FORMATTER))
					.max(naturalOrder())
					.orElse(null);
		}
	}

	private void sync(List<Path> unsyncSongs) throws SongSyncingException, IOException
	{
		Path syncFolder = generateSyncFolder();

		int longestSongName = unsyncSongs.stream()
				.mapToInt(file -> file.getFileName().toString().length())
				.max()
				.getAsInt(); //safe - this method is not called when song list is empty

		String separator = "-".repeat(18 + longestSongName + String.valueOf(unsyncSongs.size()).length());

		System.out.println(separator);

		for(int i = 0; i < unsyncSongs.size(); i++) 
		{
			Path songFolder = unsyncSongs.get(i);

			System.out.printf("Syncing \"%s\"%s(%d/%d)%n",
					songFolder.getFileName(),
					" ".repeat(longestSongName - songFolder.getFileName().toString().length() + 5),
					i+1,
					unsyncSongs.size());

			sync(songFolder, syncFolder);
		}

		System.out.println(separator);
	}

	private void sync(Path songFolder, Path syncFolder)
	{
		try
		{
			FileUtils.copyDirectoryToDirectory(songFolder.toFile(), syncFolder.toFile());
		}
		catch(Exception exception)
		{
			throw new SongSyncingException(songFolder);
		}
	}

	private Path getDataFolder() throws IOException
	{
		return Files.createDirectories(this.gameFolder.resolve("Beatmap Syncer"));
	}

	private static Path getSongsFolder(Path gameFolder)
	{
		return gameFolder.resolve("Songs");
	}

	private Path generateSyncFolder() throws IOException
	{
		String folderName = LocalDateTime.now().format(SYNC_DATE_FORMATTER);

		return Files.createDirectories(this.dataFolder.resolve(folderName));
	}
}