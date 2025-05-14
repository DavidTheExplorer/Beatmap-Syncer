package dte.beatmapsyncer.beatmap;

import dte.beatmapsyncer.exceptions.BeatmapScanningException;
import dte.beatmapsyncer.utils.FileUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

public class BeatmapScanner
{
    private final Path beatmapFolder;

    public BeatmapScanner(Path beatmapFolder)
    {
        this.beatmapFolder = beatmapFolder;
    }

    public List<Beatmap> scanUnsync(LocalDateTime lastSyncDate)
    {
        try(Stream<Path> stream = Files.list(this.beatmapFolder))
        {
            return stream
                    .filter(Files::isDirectory)
                    .filter(folder -> FileUtils.getLastModified(folder).isAfter(lastSyncDate))
                    .map(Beatmap::fromFolder)
                    .toList();
        }
        catch(Exception exception)
        {
            throw new BeatmapScanningException(exception);
        }
    }
}
