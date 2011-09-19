package de.jfruit.jmapper.ext;

import java.lang.annotation.Annotation;
import java.util.Map;

import de.jfruit.jmapper.MethodType;

public interface MapperModule<T extends Annotation> 
{
	Object executeModule(MethodType type, T anno, Map<Object, Object> prop, Object[] parameter, Class<?> expectedReturnType, Object worstCaseResult);
}
