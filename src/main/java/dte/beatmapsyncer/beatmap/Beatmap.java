package dte.beatmapsyncer.beatmap;

import java.nio.file.Files;
import java.nio.file.Path;

public record Beatmap(String name, Path folder)
{
    public static Beatmap fromFolder(Path folder)
    {
        if(!Files.isDirectory(folder))
            throw new IllegalArgumentException(String.format("The provided beatmap path('%s') is not a folder.", folder));

        //skips the beatmap id
        String name = folder.getFileName().toString()
                .transform(folderName -> folderName.substring(folderName.indexOf(' ') +1));

        return new Beatmap(name, folder);
    }
}
