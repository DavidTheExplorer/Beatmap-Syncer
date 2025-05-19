package dte.beatmapsyncer.beatmap.syncer;

import dte.beatmapsyncer.beatmap.Beatmap;
import dte.beatmapsyncer.exceptions.BeatmapSyncingException;
import dte.beatmapsyncer.exceptions.BeatmapTrackingException;
import dte.beatmapsyncer.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static java.util.Comparator.naturalOrder;

public class LocalBeatmapSyncer implements BeatmapSyncer
{
    private final Path dataFolder;

    private static final DateTimeFormatter SYNC_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy - HH.mm.ss");

    public LocalBeatmapSyncer(Path dataFolder)
    {
        this.dataFolder = dataFolder;
    }

    @Override
    public void sync(Beatmap beatmap, Context context)
    {
        try
        {
            Path syncFolder = generateSyncFolder(context.date());

            FileUtils.copyFolder(beatmap.folder(), syncFolder);
        }
        catch(Exception exception)
        {
            throw new BeatmapSyncingException(exception);
        }
    }

    @Override
    public LocalDateTime checkLastSyncDate()
    {
        try(Stream<Path> stream = Files.list(this.dataFolder))
        {
            return stream
                    .map(Path::getFileName)
                    .map(fileName -> LocalDateTime.parse(fileName.toString(), SYNC_DATE_FORMATTER))
                    .max(naturalOrder())
                    .orElse(null);
        }
        catch(Exception exception)
        {
            throw new BeatmapSyncingException(exception);
        }
    }

    @Override
    public void startTracking()
    {
        String folderName = LocalDateTime.now().format(SYNC_DATE_FORMATTER);

        try
        {
            Files.createDirectories(this.dataFolder.resolve(folderName));
        }
        catch(Exception exception)
        {
            throw new BeatmapTrackingException(exception);
        }
    }

    private Path generateSyncFolder(LocalDateTime syncDate) throws IOException
    {
        String folderName = syncDate.format(SYNC_DATE_FORMATTER);

        return Files.createDirectories(this.dataFolder.resolve(folderName));
    }
}
