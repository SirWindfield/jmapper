package de.jfruit.jmapper.ext;

import java.lang.annotation.Annotation;
import java.util.Map;

import de.jfruit.jmapper.MethodType;

public interface MapperPlugin<T extends Annotation> 
{
	Object executePlugin(MethodType type, T anno, Map<Object, Object> prop, Object[] parameter, Object currentValue, Object worstCaseResult);
}
