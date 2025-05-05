package dte.beatmapsyncer.utils.picoli;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import picocli.CommandLine.IDefaultValueProvider;
import picocli.CommandLine.Model.ArgSpec;
import picocli.CommandLine.Model.OptionSpec;

public abstract class AbstractDefaultValueProvider implements IDefaultValueProvider
{
	private final Map<String, IDefaultValueProvider> options = new HashMap<>();
	
	@Override
	public String defaultValue(ArgSpec argSpec) throws Exception 
	{
		if(argSpec.isOption())
			return getDefaultValue(this.options, ((OptionSpec) argSpec).longestName(), argSpec);
		
		return null;
	}
	
	protected void forOption(String longestName, IDefaultValueProvider provider) 
	{
		this.options.put(longestName, provider);
	}
	
	protected void forOption(String longestName, Supplier<String> provider) 
	{
		forOption(longestName, argSpec -> provider.get());
	}
	
	private <K> String getDefaultValue(Map<K, IDefaultValueProvider> map, K key, ArgSpec argSpec) throws Exception
	{
		IDefaultValueProvider provider = map.get(key);

		if(provider == null)
			return null;

		return provider.defaultValue(argSpec);
	}
}
