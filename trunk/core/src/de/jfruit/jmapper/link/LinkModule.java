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
	public Object executeModule(final MethodType type, final Link anno, final Map<Object, Object> prop, final Object[] parameter, final Class<?> expectedReturnType, final Object worstCaseResult) 
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
	
	private Object methodGetter(final Link link, final Map<Object,Object> prop, final Class<?> linkParserClass, final Object rwc) throws Exception
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
	
	private Object methodSetter(final Link link, final Object[] params, final Map<Object, Object> prop,final Class<?> parserClass, final Object rwc) throws Exception
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
	
	private ILinkParser<?,?> cachedInstance(final Class<?> ilpClass) throws Exception
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

	@Override
	public void updateCall(final MethodType type, final Link anno, final Map<Object, Object> prop, final Object[] param, final Object newValue, final int plgCount) 
	{			
		if(anno.plugins() && type==MethodType.SETTER && plgCount>0) {
			try {
				Object[] use=param;
				if(param.length>0)
					use[0]=newValue;
				else
					use=new Object[] {newValue};
				
				methodSetter(anno, use, prop, anno.parser(), prop.get(anno.key()));
			} catch (Exception e) {
				e.printStackTrace();
			}			
		}
	}
}
