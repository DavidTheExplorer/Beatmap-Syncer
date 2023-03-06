package dte.beatmapsyncer.syncer;

import static dte.beatmapsyncer.utils.StringUtils.repeat;
import static java.util.Comparator.naturalOrder;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dte.beatmapsyncer.exceptions.SongSyncingException;
import dte.beatmapsyncer.utils.SyncUtils;

public class UnsyncSongsSyncer 
{
	private final List<File> unsyncedSongs;
	private final File dataFolder;
	
	private static final Logger LOGGER = LogManager.getLogger(UnsyncSongsSyncer.class);
	
	private UnsyncSongsSyncer(Builder builder) 
	{
		this.unsyncedSongs = builder.unsyncedSongs;
		this.dataFolder = builder.dataFolder;
	}
	
	public void sync() throws SongSyncingException
	{
		File syncFolder = SyncUtils.generateSyncFolder(this.dataFolder);

		int longestSongName = this.unsyncedSongs.stream()
				.map(file -> file.getName().length())
				.max(naturalOrder())
				.get();

		String separator = repeat("-", 18 + longestSongName + String.valueOf(this.unsyncedSongs.size()).length());

		LOGGER.info(separator);

		for(int i = 0; i < this.unsyncedSongs.size(); i++) 
		{
			File songFolder = this.unsyncedSongs.get(i);

			LOGGER.info("Syncing \"{}\"{}({}/{})", 
					songFolder.getName(),
					repeat(" ", longestSongName - songFolder.getName().length() + 5),
					i+1,
					this.unsyncedSongs.size());
			
			try 
			{
				FileUtils.copyDirectoryToDirectory(songFolder, syncFolder);
			}
			catch(Exception exception) 
			{
				throw new SongSyncingException(songFolder);
			}
		}

		LOGGER.info(separator);
	}
	
	
	
	public static class Builder 
	{
		List<File> unsyncedSongs;
		File dataFolder;
		
		public Builder forSongs(List<File> unsyncedSongs) 
		{
			this.unsyncedSongs = unsyncedSongs;
			return this;
		}
		
		public Builder to(File dataFolder) 
		{
			this.dataFolder = dataFolder;
			return this;
		}
		
		public UnsyncSongsSyncer build() 
		{
			return new UnsyncSongsSyncer(this);
		}
	}
}
