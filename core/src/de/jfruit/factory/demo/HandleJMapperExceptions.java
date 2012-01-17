package de.jfruit.factory.demo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import de.jfruit.jmapper.JMapper;
import de.jfruit.jmapper.JMapperExceptionHandler.InvalidAnnotation;
import de.jfruit.jmapper.JMapperExceptionHandler.JMapperException;
import de.jfruit.jmapper.MethodType;
import de.jfruit.jmapper.ext.MapperPlugin;

public class HandleJMapperExceptions 
{
	public static void main(final String[] args) 
	{				
		// Will throw a RuntimeException
		//JMapper.install(InvalidPluginAnnotation.class, new InvalidPluginImpl());
		
		
		try {
			JMapper.install(InvalidPluginAnnotation.class, InvalidPluginImpl.class);			
		} catch (InvalidAnnotation e) {
			System.out.println("React on Invalid Annotation");			
		} catch (JMapperException e) {
			System.out.println("Catch All!");
		}
		
	}
	
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface InvalidPluginAnnotation {}
	
	public static class InvalidPluginImpl implements MapperPlugin<InvalidPluginAnnotation> 
	{
		@Override
		public Object executePlugin(final MethodType type,
				final InvalidPluginAnnotation anno, final Map<Object, Object> prop,
				final Object[] parameter, final Object currentValue, final Object worstCaseResult) {
			return currentValue;
		}
	}
}
