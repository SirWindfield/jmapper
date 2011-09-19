package de.jfruit.jmapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.jfruit.jmapper.adapter.Adapter;
import de.jfruit.jmapper.adapter.AdapterModule;
import de.jfruit.jmapper.ext.MapperModule;
import de.jfruit.jmapper.ext.MapperPlugin;
import de.jfruit.jmapper.link.Link;
import de.jfruit.jmapper.link.LinkModule;

/**
 * JMapper is able to transform a {@link Map} into an Interface. 
 * The methods of the interface require some Annotation like {@link Link} or {@link Adapter}, so 
 * JMapper knows, how to combine Interface and Map.<br>
 * <br>
 * You're able to write your own modules or plugins. If you want to use them, you have to
 * install them by calling {@link #install(Class, Class)}, {@link #install(Class, MapperModule)} or {@link #install(Class, MapperPlugin)}<br>
 * For more information about modules see {@link MapperModule} and for plugins see {@link MapperPlugin}
 * 
 * @author Tomate_Salat 
 */
@SuppressWarnings({ "unchecked" })
public class JMapper 
{		
	private JMapper() {};
	private static Map<Class<? extends Annotation>, MapperModule<? extends Annotation>> modules=new HashMap<Class<? extends Annotation>, MapperModule<? extends Annotation>>();
	private static Map<Class<? extends Annotation>, MapperPlugin<? extends Annotation>> plugins=new HashMap<Class<? extends Annotation>, MapperPlugin<? extends Annotation>>();
	
	/**
	 * This method will install you're Plugin or Module and register it with the given Annotation.
	 */
	public static <T extends Annotation> void install(Class<T> annotation, Class<?> instClass)
	{
		try {
			for(Type genType : instClass.getGenericInterfaces()) {
				ParameterizedType type=(ParameterizedType) genType;				
				for(Type realType : type.getActualTypeArguments()) {					
					if(realType==annotation) {
						Constructor<?> constr=instClass.getDeclaredConstructor();
						constr.setAccessible(true);						
						
						if(type.getRawType() == MapperModule.class) {
							modules.put(annotation, (MapperModule<? extends Annotation>) constr.newInstance());
						}
						if(type.getRawType() == MapperPlugin.class) {
							plugins.put(annotation, (MapperPlugin<? extends Annotation>) constr.newInstance());
						}
						return;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will install you're Plugin or Module and register it with the given Annotation.
	 */
	public static <T extends Annotation> void install(Class<T> annotation, MapperModule<? extends T> module)
	{
		modules.put(annotation, module);
	}
	
	/**
	 * This method will install you're Plugin or Module and register it with the given Annotation.
	 */
	public static <T extends Annotation> void install(Class<T> annotation, MapperPlugin<? extends T> module)
	{
		plugins.put(annotation, module);
	}
	
	static {
		install(Adapter.class, new AdapterModule());
		install(Link.class, new LinkModule());
	}
	
	/**
	 * Call this method for getting an interface depending on the map. 
	 * The interface will work on the real Map on with the registered modules/plugins (even if you will install them
	 * after calling this method).
	 */
	public static <T> T createFromMap(final Map<Object, Object> prop, Class<T> interf)
	{
		return (T) Proxy.newProxyInstance(JMapper.class.getClassLoader(), new Class<?>[]{interf}, new InvocationHandler() {
			
			@Override
			public Object invoke(Object obj, Method method, Object[] params) throws Throwable 
			{
				Class<?> returnType=method.getReturnType();
				Object resultInWorstCase=(returnType.isPrimitive()) ? ((returnType==Boolean.class || returnType==boolean.class) ? false : 0) : null;				
								
				MethodType methodType=findMethodType(method,false);
				
				Object moduleResult=resultInWorstCase;
				
				for(Entry<Class<? extends Annotation>, MapperModule<? extends Annotation>> mod : modules.entrySet())
				{
					if(method.isAnnotationPresent(mod.getKey())) {
						moduleResult=((MapperModule<Annotation>)mod.getValue()).executeModule(methodType, method.getAnnotation(mod.getKey()), prop, params, returnType, resultInWorstCase);
						
						if(methodType.equals(MethodType.GETTER) && moduleResult!=resultInWorstCase && !checkTypes(returnType, moduleResult.getClass())) {
							moduleResult=resultInWorstCase;							
						}						
						break;
					}
				}
				
				for(Entry<Class<? extends Annotation>, MapperPlugin<? extends Annotation>> plugin : plugins.entrySet())
				{
					if(method.isAnnotationPresent(plugin.getKey())) {
						
						Object tmpResult=((MapperPlugin<Annotation>)plugin.getValue()).executePlugin(methodType, method.getAnnotation(plugin.getKey()), prop, params, moduleResult, resultInWorstCase);
						
						if(returnType.isAssignableFrom(tmpResult.getClass()))
							moduleResult=tmpResult;
					}
				}
				
				return moduleResult;
			}
		});
	}	
	
	private static MethodType findMethodType(Method currentMethod, boolean disableVoidCheck)
	{
		if(!disableVoidCheck) {
			if(currentMethod.getReturnType().equals(Void.class))
				return MethodType.SETTER;			
		}
		
		if(currentMethod.getName().toLowerCase().startsWith("set"))
			return MethodType.SETTER;
		
		return MethodType.GETTER;
	}
	
	private static boolean checkTypes(Class<?> expected, Class<?> resultType) 
	{
		if(!expected.isPrimitive())
			return expected.isAssignableFrom(resultType);

		Class<?> types[][] = {
				{Integer.class, Integer.TYPE},
				{Float.class, Float.TYPE},
				{Double.class, Double.TYPE},
				{Long.class, Long.TYPE},
				{Boolean.class, Boolean.TYPE},
				{Character.class, Character.TYPE}
		};
		
		for(Class<?>[] ck : types) {
			if(ck[0].isAssignableFrom(resultType) && ck[1].equals(expected))
				return true;
		}
			
		return false;
	}
}
