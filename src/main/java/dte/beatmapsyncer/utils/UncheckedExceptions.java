package dte.beatmapsyncer.utils;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/*
 * Before any complaints, overloading all methods to be named "unchecked" sometimes causes compile errors due to ambiguous parameters.
 */
public class UncheckedExceptions
{
	public static Runnable uncheckedRun(CheckedRunnable runnable)
	{
		return () -> 
		{
			try
			{
				runnable.run();
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T> Supplier<T> uncheckedGet(CheckedSupplier<T> supplier)
	{
		return () -> 
		{
			try
			{
				return supplier.get();
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T> Consumer<T> uncheckedAccept(CheckedConsumer<T> consumer)
	{
		return object -> 
		{
			try
			{
				consumer.accept(object);
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	
	public static <T, R> Predicate<T> uncheckedTest(CheckedPredicate<T> predicate)
	{
		return object -> 
		{
			try 
			{
				return predicate.test(object);
			}
			catch(Exception exception) 
			{
				throw new RuntimeException(exception);
			}
		};
	}
	


	@FunctionalInterface
	public static interface CheckedRunnable
	{
		void run() throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedConsumer<T>
	{
		void accept(T object) throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedSupplier<T>
	{
		T get() throws Exception;
	}
	
	@FunctionalInterface
	public static interface CheckedPredicate<T>
	{
		boolean test(T object) throws Exception;
	}
}
