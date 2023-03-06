package dte.beatmapsyncer.detector;

import static dte.beatmapsyncer.utils.UncheckedExceptions.uncheckedTest;
import static java.util.stream.Collectors.groupingBy;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import dte.beatmapsyncer.utils.DateUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnsyncSongsDetector
{
	private final File songsFolder;
	
	public Map<SyncType, List<File>> searchAfter(LocalDateTime lastSyncDate)
	{
		return Arrays.stream(this.songsFolder.listFiles())
				.filter(File::isDirectory)
				.filter(uncheckedTest(songFolder -> DateUtils.getLastModified(songFolder).isAfter(lastSyncDate)))
				.collect(groupingBy(songFolder -> SyncType.CHANGED));
	}
}
