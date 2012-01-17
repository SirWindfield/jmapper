package de.jfruit.factory.demo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Properties;

import de.jfruit.jmapper.JMapper;
import de.jfruit.jmapper.MethodType;
import de.jfruit.jmapper.ext.MapperModule;
import de.jfruit.jmapper.ext.MapperPlugin;
import de.jfruit.jmapper.ext.ModuleExtension;
import de.jfruit.jmapper.ext.PluginExtension;
import de.jfruit.jmapper.link.Link;

public class PluginDemo 
{
	public static void main(final String[] args) 
	{
		Properties props=new Properties();
		props.setProperty("test", "hallo");
		
		JMapper.install(Version.class, MyExtension.class);		
		
		Config conf=JMapper.createFromMap(props, Config.class);
		System.out.println("Version     : " + conf.version());
		System.out.println("AlphaVersion: " + conf.alphaVersion());
		System.out.println("AlphaTest   : " + conf.alphaTest());
		
		System.out.println("---");
		
		JMapper.install(Alpha.class, MyExtension.class); // <-- Sofort wirksam		
		System.out.println("Version     : " + conf.version());
		System.out.println("AlphaVersion: " + conf.alphaVersion());
		System.out.println("AlphaTest   : " + conf.alphaTest());
	}
	
	public static interface Config 
	{
		@Version
		String version();
		
		@Alpha @Version
		String alphaVersion();
		
		@Alpha @Link(key="test")
		String alphaTest();
	}
	
	@PluginExtension @Retention(RetentionPolicy.RUNTIME)
	public static @interface Alpha {};	
	@ModuleExtension @Retention(RetentionPolicy.RUNTIME)
	public static @interface Version { String value() default "1.0"; }
	
	public static class MyExtension implements MapperModule<Version>, MapperPlugin<Alpha>		
	{

		@Override
		public Object executeModule(final MethodType type, final Version anno, final Map<Object, Object> prop,
				final Object[] parameter, final Class<?> expectedReturnType,
				final Object worstCaseResult) {
			return anno.value();
		}

		@Override
		public Object executePlugin(final MethodType type, final Alpha anno, final Map<Object, Object> prop,
				final Object[] parameter, final Object currentValue, final Object worstCaseResult) {
			return currentValue + "_alpha";
		}

		@Override
		public void updateCall(final MethodType type, final Version anno,
				final Map<Object, Object> prop, final Object[] parameter, final Object newValue,
				final int plgCount) {
			// TODO Auto-generated method stub
			
		}

	}
}
