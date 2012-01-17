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

public class InstallExtDemo
{
	public static void main(final String[] args) 
	{
		// Will find and bind the Module/Plugin to the given Annotation		
		JMapper.installExt(MyExtension.class);
		
		Properties props=new Properties();
		
		MyDemo demo=JMapper.createFromMap(props, MyDemo.class);
		demo.setData("Hallo");
		
		System.out.println(demo.getData());
		System.out.println(demo.getDefault());
	}
	
	public static interface MyDemo 
	{
		@MyModule
		String getDefault();
		
		//@MyPlugin
		@Link(key="data")
		String getData();
		
		@MyPlugin		
		@Link(key="data", plugins=true)		
		void setData(String s);
	}
	
	@PluginExtension 
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface MyPlugin {}
	
	@ModuleExtension
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface MyModule {}
	
	public static class MyExtension implements MapperPlugin<MyPlugin>, MapperModule<MyModule>
	{

		@Override
		public Object executeModule(final MethodType type, final MyModule anno,
				final Map<Object, Object> prop, final Object[] parameter,
				final Class<?> expectedReturnType, final Object worstCaseResult) 
		{
			return worstCaseResult;
		}

		@Override
		public Object executePlugin(final MethodType type, final MyPlugin anno,
				final Map<Object, Object> prop, final Object[] parameter,
				final Object currentValue, final Object worstCaseResult) 
		{					
			return currentValue+"_extend";
		}

		@Override
		public void updateCall(final MethodType type, final MyModule anno,
				final Map<Object, Object> prop, final Object[] parameter, final Object newValue,
				final int plgCount) {
			// TODO Auto-generated method stub
			
		}

		
	}
}
