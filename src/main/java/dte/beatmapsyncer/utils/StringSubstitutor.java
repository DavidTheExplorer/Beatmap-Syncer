package dte.beatmapsyncer.utils;

import java.util.HashMap;
import java.util.Map;

public class StringSubstitutor
{
	private String initialText;
	private final Map<String, Object> placeholders = new HashMap<>();
	
	private static final String PLACEHOLDER_FORMAT = "${%s}";
	
	public StringSubstitutor(String initialText) 
	{
		this.initialText = initialText;
	}
	
	public StringSubstitutor inject(String placeholder, Object value) 
	{
		this.placeholders.put(String.format(PLACEHOLDER_FORMAT, placeholder), value);
		return this;
	}
	
	public String apply() 
	{
		this.placeholders.forEach((placeholder, value) -> this.initialText = this.initialText.replace(placeholder, value.toString()));
		
		return this.initialText;
	}
	
	@Override
	public String toString() 
	{
		return apply();
	}
}
