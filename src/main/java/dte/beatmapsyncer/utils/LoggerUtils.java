package dte.beatmapsyncer.utils;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LoggerUtils
{
	public static Logger newConsoleLogger(String name) 
	{
		Logger logger = Logger.getLogger(name);
		logger.setUseParentHandlers(false);
		logger.addHandler(new SingleLineConsoleHandler());
		
		return logger;
	}
	
	
	
	private static class SingleLineConsoleHandler extends ConsoleHandler
	{
		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
				.withZone(ZoneId.systemDefault());
		
		public SingleLineConsoleHandler() 
		{
			setFormatter(new Formatter() 
			{
				@Override
				public String format(LogRecord record) 
				{
					return String.format("[%s] %s: %s\n", FORMATTER.format(record.getInstant()), record.getLevel().getName(), formatMessage(record));
				}
			});
		}
	}
}