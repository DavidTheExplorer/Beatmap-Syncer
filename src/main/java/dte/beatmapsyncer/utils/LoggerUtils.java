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
		logger.addHandler(new BetterConsoleHandler());
		
		return logger;
	}
	
	
	
	private static class BetterConsoleHandler extends ConsoleHandler
	{
		public BetterConsoleHandler() 
		{
			//disable logging 2 lines every time
			setFormatter(new LineFormatter());
			
			//prevent red output(System.err)
			setOutputStream(System.out);
		}
	}
	
	private static class LineFormatter extends Formatter
	{
		private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
				.withZone(ZoneId.systemDefault());
		
		@Override
		public String format(LogRecord record) 
		{
			return String.format("[%s] %s: %s\n", FORMATTER.format(record.getInstant()), record.getLevel().getName(), formatMessage(record));
		}
	}
}
