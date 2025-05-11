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

import dte.beatmapsyncer.beatmap.Beatmap;
import dte.beatmapsyncer.exceptions.BeatmapSyncingException;
import org.apache.commons.io.FileUtils;

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

		System.out.printf("Searching unsync beatmaps... (Last sync was %s)%n", SYNC_DATE_DISPLAY_FORMATTER.format(this.lastSyncDate));

		List<Beatmap> unsyncBeatmaps = searchUnsyncBeatmaps();

		if(unsyncBeatmaps.isEmpty())
		{
			System.out.println("No beatmaps found!");
			return OK;
		}

		System.out.println();
		sync(unsyncBeatmaps);
		System.out.println();
		System.out.println("Successfully synced everything!");
		return OK;
	}

	@Option(names = "-gameFolder")
	public void setGameFolder(Path gameFolder)
	{
		if(!Files.exists(gameFolder))
			throw new ParameterException(this.commandSpec.commandLine(), "The provided osu! folder \"%s\" couldn't be found.".formatted(gameFolder));

		if(!Files.exists(getBeatmapFolder(gameFolder)))
			throw new ParameterException(this.commandSpec.commandLine(), "The provided osu! folder \"%s\" doesn't have a beatmap folder.".formatted(gameFolder));

		this.gameFolder = gameFolder;
	}

	private List<Beatmap> searchUnsyncBeatmaps() throws IOException
	{
		try(Stream<Path> stream = Files.list(getBeatmapFolder(this.gameFolder)))
		{
			return stream
					.filter(Files::isDirectory)
					.filter(folder -> DateUtils.getLastModified(folder).isAfter(this.lastSyncDate))
					.map(Beatmap::fromFolder)
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

	private void sync(List<Beatmap> unsyncBeatmaps) throws IOException
	{
		Path syncFolder = generateSyncFolder();

		int longestBeatmapName = unsyncBeatmaps.stream()
				.mapToInt(beatmap -> beatmap.getName().length())
				.max()
				.getAsInt(); //safe - this method is not called when the list is empty

		String separator = "-".repeat(18 + longestBeatmapName + String.valueOf(unsyncBeatmaps.size()).length());

		System.out.println(separator);

		for(int i = 0; i < unsyncBeatmaps.size(); i++)
		{
			Beatmap beatmap = unsyncBeatmaps.get(i);

			System.out.printf("Syncing \"%s\"%s(%d/%d)%n",
					beatmap.getName(),
					" ".repeat(longestBeatmapName - beatmap.getName().length() + 5),
					i+1,
					unsyncBeatmaps.size());

			sync(beatmap, syncFolder);
		}

		System.out.println(separator);
	}

	private void sync(Beatmap beatmap, Path syncFolder)
	{
		try
		{
			FileUtils.copyDirectoryToDirectory(beatmap.getFolder().toFile(), syncFolder.toFile());
		}
		catch(Exception exception)
		{
			throw new BeatmapSyncingException(beatmap);
		}
	}

	private Path getDataFolder() throws IOException
	{
		return Files.createDirectories(this.gameFolder.resolve("Beatmap Syncer"));
	}

	private static Path getBeatmapFolder(Path gameFolder)
	{
		return gameFolder.resolve("Songs");
	}

	private Path generateSyncFolder() throws IOException
	{
		String folderName = LocalDateTime.now().format(SYNC_DATE_FORMATTER);

		return Files.createDirectories(this.dataFolder.resolve(folderName));
	}
}