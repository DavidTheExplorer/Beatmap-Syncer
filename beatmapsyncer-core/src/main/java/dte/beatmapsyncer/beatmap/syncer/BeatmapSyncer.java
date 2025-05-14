package dte.beatmapsyncer.beatmap.syncer;

import dte.beatmapsyncer.beatmap.Beatmap;

import java.time.LocalDateTime;

public interface BeatmapSyncer
{
    void sync(Beatmap beatmap, Context context);
    LocalDateTime checkLastSyncDate();
    void startTracking();



    record Context(LocalDateTime date){}
}