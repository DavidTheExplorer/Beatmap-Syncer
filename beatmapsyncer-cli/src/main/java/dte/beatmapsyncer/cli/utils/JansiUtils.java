package dte.beatmapsyncer.cli.utils;

import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine.Help.Ansi;

public class JansiUtils
{
    static
    {
        AnsiConsole.systemInstall();

        Runtime.getRuntime().addShutdownHook(new Thread(AnsiConsole::systemUninstall));
    }

    public static String colorize(String text)
    {
        return Ansi.AUTO.string(text);
    }
}
