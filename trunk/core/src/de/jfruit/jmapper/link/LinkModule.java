package de.jfruit.jmapper.link;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import de.jfruit.jmapper.MethodType;
import de.jfruit.jmapper.NullParser;
import de.jfruit.jmapper.ext.MapperModule;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class LinkModule implements MapperModule<Link>
{
	private static Map<Class<?>, ILinkParser<?,?>> parserCache=new HashMap<Class<?>, ILinkParser<?,?>>();
	
	@Override
	public Object executeModule(MethodType type, Link anno, Map<Object, Object> prop, Object[] parameter, Class<?> expectedReturnType, Object worstCaseResult) 
	{
		if(prop==null)
			return worstCaseResult;
		
		try {
			Class<?> parserClass=anno.parser();
			
			switch(type) {
				case GETTER:
					return methodGetter(anno,prop,parserClass,worstCaseResult);
				case SETTER:
					return methodSetter(anno,parameter,prop,parserClass,worstCaseResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return worstCaseResult;
	}	
	
	private Object methodGetter(Link link, Map<Object,Object> prop, Class<?> linkParserClass, Object rwc) throws Exception
	{
		String key=link.key();
		
		if(linkParserClass==NullParser.class) {
			if(prop.containsKey(key))
				return prop.get(key);
			return rwc;
		}
		
		ILinkParser parser=cachedInstance(linkParserClass);
		
		if(prop.containsKey(key))
			return parser.parse(prop.get(key));
		
		return parser.defValue();
	}		
	
	private Object methodSetter(Link link, Object[] params, Map<Object, Object> prop,Class<?> parserClass, Object rwc) throws Exception
	{
		if(params.length!=1)
			return rwc;			
		
		if(parserClass==NullParser.class) {
			prop.put(link.key(), params[0]);
			return params[0];
		}
				
		ILinkParser parser=cachedInstance(parserClass);

		prop.put(link.key(), parser.parse(params[0]));
		
		return params[0];
	}
	
	private ILinkParser<?,?> cachedInstance(Class<?> ilpClass) throws Exception
	{
		if(!parserCache.containsKey(ilpClass)) {
			Constructor<?> constr=ilpClass.getDeclaredConstructor();
			constr.setAccessible(true);
			ILinkParser<?,?> adapter=(ILinkParser<?,?>)constr.newInstance();
			parserCache.put(ilpClass, adapter);
			return adapter;
		}
		
		return parserCache.get(ilpClass);
	}
}
