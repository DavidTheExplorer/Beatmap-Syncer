package dte.beatmapsyncer.beatmap;

import java.nio.file.Files;
import java.nio.file.Path;

public class Beatmap
{
    private final String name;
    private final Path folder;

    private Beatmap(String name, Path folder)
    {
        this.name = name;
        this.folder = folder;
    }

    public static Beatmap fromFolder(Path folder)
    {
        if(!Files.isDirectory(folder))
            throw new IllegalArgumentException(String.format("The provided beatmap path('%s') is not a folder.", folder));

        return new Beatmap(extractName(folder), folder);
    }

    private static String extractName(Path beatmapFolder)
    {
        String folderName = beatmapFolder.getFileName().toString();

        //skips the beatmap id
        return folderName.substring(folderName.indexOf(' ') +1);
    }

    public String getName()
    {
        return this.name;
    }

    public Path getFolder()
    {
        return this.folder;
    }
}
