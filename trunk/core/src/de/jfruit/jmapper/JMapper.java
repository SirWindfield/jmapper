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

import de.jfruit.jmapper.JMapperExceptionHandler.InvalidAnnotation;
import de.jfruit.jmapper.JMapperExceptionHandler.JMapperException;
import de.jfruit.jmapper.adapter.Adapter;
import de.jfruit.jmapper.adapter.AdapterModule;
import de.jfruit.jmapper.ext.MapperModule;
import de.jfruit.jmapper.ext.MapperPlugin;
import de.jfruit.jmapper.ext.ModuleExtension;
import de.jfruit.jmapper.ext.PluginExtension;
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
	 * 
	 * @throws
	 * InvalidAnnotation if the Annotation isn't valid
	 * @throws
	 * JMapperException 
	 */
	public static <T extends Annotation> void install(final Class<T> annotation, final Class<?> instClass)
	{
		try {
			for(Type genType : instClass.getGenericInterfaces()) {
				ParameterizedType type=(ParameterizedType) genType;				
				for(Type realType : type.getActualTypeArguments()) {					
					if(realType==annotation) {
						Constructor<?> constr=instClass.getDeclaredConstructor();
						constr.setAccessible(true);						
						
						if(type.getRawType() == MapperModule.class) {
							if(annotation.isAnnotationPresent(ModuleExtension.class))
								modules.put(annotation, (MapperModule<? extends Annotation>) constr.newInstance());
							else
								throw new JMapperExceptionHandler.InvalidAnnotation(annotation, ModuleExtension.class);
						} else if(type.getRawType() == MapperPlugin.class) {
							if(annotation.isAnnotationPresent(PluginExtension.class))
								plugins.put(annotation, (MapperPlugin<? extends Annotation>) constr.newInstance());
							else								
								throw new JMapperExceptionHandler.InvalidAnnotation(annotation, PluginExtension.class);
						}
						return;
					}
				}
			}
		} catch (JMapperException e)  {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void installExt(final Class<?> instClass) 
	{
		try {
			for(Type genType : instClass.getGenericInterfaces()) {
				ParameterizedType type=(ParameterizedType) genType;
				for(Type realType : type.getActualTypeArguments()) {
					Class<?> getType=(Class<?>) realType;
					if(getType.isAnnotation()) {
						Class<? extends Annotation> annotatedType=(Class<? extends Annotation>) getType;
						
						if((getType.isAnnotationPresent(PluginExtension.class) && ((Class<?>)type.getRawType()).isAssignableFrom(MapperPlugin.class)) ||
						   (getType.isAnnotationPresent(ModuleExtension.class) && ((Class<?>)type.getRawType()).isAssignableFrom(MapperModule.class))) 
						{
							installIntern(annotatedType, instClass);
							continue;
						} 
					}
				}
			}
		} catch (JMapperException e) {
			throw e;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will install you're Plugin or Module and register it with the given Annotation.
	 * 
	 * @throws
	 * InvalidAnnotation
	 */
	public static <T extends Annotation> void install(final Class<T> annotation, final MapperModule<? extends T> module)
	{
		if(annotation.isAnnotationPresent(ModuleExtension.class))
			modules.put(annotation, module);
		else
			throw new JMapperExceptionHandler.InvalidAnnotation(annotation, ModuleExtension.class);
	}
	
	/**
	 * This method will install you're Plugin or Module and register it with the given Annotation.
	 * 
	 * @throws
	 * InvalidAnnotation
	 */
	public static <T extends Annotation> void install(final Class<T> annotation, final MapperPlugin<? extends T> module)
	{
		if(annotation.isAnnotationPresent(PluginExtension.class))
			plugins.put(annotation, module);
		else 
			throw new JMapperExceptionHandler.InvalidAnnotation(annotation, PluginExtension.class);
	}
	
	private static <T extends Annotation> void installIntern(final Class<T> annotation, final Class<?> extension)
	{
		Object instance=null;
		
		try {
			Constructor<?> constr=extension.getDeclaredConstructor();
			constr.setAccessible(true);
			
			instance=constr.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(annotation.isAnnotationPresent(PluginExtension.class))
			plugins.put(annotation, (MapperPlugin<? extends Annotation>) instance);
		else if(annotation.isAnnotationPresent(ModuleExtension.class))
			modules.put(annotation, (MapperModule<? extends Annotation>) instance);
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
	public static <T> T createFromMap(final Map<Object, Object> prop, final Class<T> interf)
	{
		return (T) Proxy.newProxyInstance(JMapper.class.getClassLoader(), new Class<?>[]{interf}, new InvocationHandler() {
			
			@Override
			public Object invoke(final Object obj, final Method method, final Object[] params) throws Throwable 
			{
				Class<?> returnType=method.getReturnType();
				Object resultInWorstCase=(returnType.isPrimitive()) ? ((returnType==Boolean.class || returnType==boolean.class) ? false : 0) : null;				
								
				MethodType methodType=findMethodType(method,false);
				
				Object moduleResult=resultInWorstCase;
				
				Annotation annotationUsed=null;
				MapperModule<Annotation> mapperMod=null;
				
				// Invoke main-Module
				for(Entry<Class<? extends Annotation>, MapperModule<? extends Annotation>> mod : modules.entrySet())
				{
					if(method.isAnnotationPresent(mod.getKey())) {
						annotationUsed=method.getAnnotation(mod.getKey());
						moduleResult=(mapperMod=(MapperModule<Annotation>)mod.getValue()).executeModule(methodType, annotationUsed, prop, params, returnType, resultInWorstCase);
						
						if(methodType.equals(MethodType.GETTER) && moduleResult!=resultInWorstCase && !checkTypes(returnType, moduleResult.getClass())) {							
							moduleResult=resultInWorstCase;							
						}						
						break;
					}
				}
								
				int plgCount=0;
				
				// Invoke the plugins
				for(Entry<Class<? extends Annotation>, MapperPlugin<? extends Annotation>> plugin : plugins.entrySet())
				{
					if(method.isAnnotationPresent(plugin.getKey())) {
						Object tmpResult=((MapperPlugin<Annotation>)plugin.getValue()).executePlugin(methodType, method.getAnnotation(plugin.getKey()), prop, params, moduleResult, resultInWorstCase);						
						
						if(!checkTypes(returnType, tmpResult.getClass())) {
							plgCount++;
							moduleResult=tmpResult;							
						}
					}
				}
				
				// Update the module
				if(mapperMod!=null && annotationUsed!=null)
					mapperMod.updateCall(methodType, annotationUsed, prop, params, moduleResult, plgCount);				
				
				return moduleResult;
			}
		});
	}	
	
	private static MethodType findMethodType(final Method currentMethod, final boolean disableVoidCheck)
	{
		if(!disableVoidCheck) {
			if(currentMethod.getReturnType().equals(Void.class))
				return MethodType.SETTER;			
		}
		
		if(currentMethod.getName().toLowerCase().startsWith("set"))
			return MethodType.SETTER;
		
		return MethodType.GETTER;
	}
	
	private static boolean checkTypes(final Class<?> expected, final Class<?> resultType) 
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
