package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.beatmap.Beatmap;
import dte.beatmapsyncer.beatmap.BeatmapScanner;
import dte.beatmapsyncer.beatmap.syncer.BeatmapSyncer;
import dte.beatmapsyncer.beatmap.syncer.BeatmapSyncer.Context;
import dte.beatmapsyncer.beatmap.syncer.LocalBeatmapSyncer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;

import static picocli.CommandLine.ExitCode.OK;

@Command(name = "beatmapsyncer", description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.")
public class BeatmapSyncerCLI implements Callable<Integer>
{
    @Option(names = "-gameFolder")
    private Path gameFolder;

    @Spec
    private CommandSpec commandSpec;

    private BeatmapScanner beatmapScanner;
    private BeatmapSyncer beatmapSyncer;

    private static final DateTimeFormatter SYNC_DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("'on' dd-MM-yyyy 'at' HH:mm:ss");

    @Override
    public Integer call() throws Exception
    {
        this.beatmapScanner = createBeatmapScanner();
        this.beatmapSyncer = createBeatmapSyncer();

        LocalDateTime lastSyncDate = this.beatmapSyncer.checkLastSyncDate();

        if(lastSyncDate == null)
        {
            System.out.println("Started tracking beatmap changes!");
            this.beatmapSyncer.startTracking();
            return OK;
        }

        System.out.printf("Searching unsync beatmaps... (Last sync was %s)%n", SYNC_DATE_DISPLAY_FORMATTER.format(lastSyncDate));

        List<Beatmap> unsyncBeatmaps = this.beatmapScanner.scanUnsync(lastSyncDate);

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

    private BeatmapScanner createBeatmapScanner()
    {
        Path beatmapFolder = this.gameFolder.resolve("Songs");

        if(!Files.isDirectory(beatmapFolder))
            throw new ParameterException(this.commandSpec.commandLine(), "The provided osu! folder \"%s\" doesn't have a beatmap folder.".formatted(this.gameFolder));

        return new BeatmapScanner(beatmapFolder);
    }

    private BeatmapSyncer createBeatmapSyncer() throws IOException
    {
        Path dataFolder = Files.createDirectories(this.gameFolder.resolve("Beatmap Syncer"));

        return new LocalBeatmapSyncer(dataFolder);
    }

    private void sync(List<Beatmap> unsyncBeatmaps)
    {
        int longestBeatmapName = unsyncBeatmaps.stream()
                .mapToInt(beatmap -> beatmap.name().length())
                .max()
                .getAsInt(); //safe - this method is not called when the list is empty

        String separator = "-".repeat(18 + longestBeatmapName + String.valueOf(unsyncBeatmaps.size()).length());

        System.out.println(separator);

        BeatmapSyncer.Context context = new Context(LocalDateTime.now());

        for(int i = 0; i < unsyncBeatmaps.size(); i++)
        {
            Beatmap beatmap = unsyncBeatmaps.get(i);

            System.out.printf("Syncing \"%s\"%s(%d/%d)%n",
                    beatmap.name(),
                    " ".repeat(longestBeatmapName - beatmap.name().length() + 5),
                    i+1,
                    unsyncBeatmaps.size());

            this.beatmapSyncer.sync(beatmap, context);
        }

        System.out.println(separator);
    }
}
