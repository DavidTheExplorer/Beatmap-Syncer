package dte.beatmapsyncer.cli.exceptions;

import picocli.CommandLine;
import picocli.CommandLine.ExitCode;
import picocli.CommandLine.IParameterExceptionHandler;
import picocli.CommandLine.ParameterException;

public class SimpleParameterExceptionHandler implements IParameterExceptionHandler
{
    @Override
    public int handleParseException(ParameterException exception, String[] args) throws Exception
    {
        CommandLine commandLine = exception.getCommandLine();
        commandLine.getErr().println(commandLine.getColorScheme().errorText("Parameter Error: " + exception.getMessage()));

        return ExitCode.USAGE;
    }
}
