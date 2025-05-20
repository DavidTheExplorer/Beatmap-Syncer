package dte.beatmapsyncer.cli;

import dte.beatmapsyncer.beatmap.Beatmap;
import dte.beatmapsyncer.beatmap.BeatmapScanner;
import dte.beatmapsyncer.beatmap.syncer.BeatmapSyncer;
import dte.beatmapsyncer.beatmap.syncer.BeatmapSyncer.Context;
import dte.beatmapsyncer.beatmap.syncer.LocalBeatmapSyncer;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
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

import static me.tongfei.progressbar.ProgressBarStyle.ASCII;
import static picocli.CommandLine.ExitCode.OK;

@Command(name = "beatmapsyncer", description = "Tracks your changed osu! beatmaps so they are updated on every machine you play on.")
public class BeatmapSyncerCLI implements Callable<Integer>
{
    @Option(names = "--gameFolder")
    private Path gameFolder;

    @Spec
    private CommandSpec commandSpec;

    private BeatmapScanner beatmapScanner;
    private BeatmapSyncer beatmapSyncer;

    private static final DateTimeFormatter SYNC_DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy 'at' HH:mm:ss");

    @Override
    public Integer call() throws Exception
    {
        this.beatmapScanner = createBeatmapScanner();
        this.beatmapSyncer = createBeatmapSyncer();

        LocalDateTime lastSyncDate = this.beatmapSyncer.checkLastSyncDate();

        if(lastSyncDate == null)
        {
            this.beatmapSyncer.startTracking();
            System.out.println("First launch detected! Started tracking beatmap changes.");
            System.out.println("You don't need to do anything, come back when a sync is needed.");
            return OK;
        }

        System.out.println("Checking for unsync beatmaps...");
        System.out.println();

        List<Beatmap> unsyncBeatmaps = this.beatmapScanner.scanUnsync(lastSyncDate);

        if(unsyncBeatmaps.isEmpty())
        {
            System.out.printf("No beatmaps were found since the last sync! (%s)", SYNC_DATE_DISPLAY_FORMATTER.format(lastSyncDate));
            return OK;
        }

        System.out.printf("%d beatmaps were found since the last sync! (%s)%n", unsyncBeatmaps.size(), SYNC_DATE_DISPLAY_FORMATTER.format(lastSyncDate));
        System.out.println("Syncing started...");
        sync(unsyncBeatmaps);
        System.out.println("Success!");
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
        BeatmapSyncer.Context context = new Context(LocalDateTime.now());

        for(Beatmap beatmap : ProgressBar.wrap(unsyncBeatmaps, createProgressBarBuilder()))
            this.beatmapSyncer.sync(beatmap, context);
    }

    private static ProgressBarBuilder createProgressBarBuilder()
    {
        return new ProgressBarBuilder()
                .setTaskName("Syncing Beatmaps")
                .setStyle(ASCII)
                .hideEta()
                .setUpdateIntervalMillis(1);
    }
}
