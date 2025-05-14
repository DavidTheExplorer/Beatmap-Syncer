package dte.beatmapsyncer.utils;

import java.util.Arrays;
import java.util.function.Predicate;

public enum OperatingSystem
{
    WINDOWS(osName -> osName.startsWith("Windows"));

    private final Predicate<String> osNameMatcher;

    OperatingSystem(Predicate<String> osNameMatcher)
    {
        this.osNameMatcher = osNameMatcher;
    }

    public static OperatingSystem detectCurrent()
    {
        String osName = System.getProperty("os.name");

        return Arrays.stream(values())
                .filter(system -> system.osNameMatcher.test(osName))
                .findFirst()
                .orElse(null);
    }
}
