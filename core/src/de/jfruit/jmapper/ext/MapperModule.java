package de.jfruit.jmapper.ext;

import java.lang.annotation.Annotation;
import java.util.Map;

import de.jfruit.jmapper.MethodType;

public interface MapperModule<T extends Annotation> 
{
	/**
	 * This method will be executed before any plugin. So the module is ought to return
	 * a usefull value.
	 */
	Object executeModule(MethodType type, T anno, Map<Object, Object> prop, Object[] parameter, Class<?> expectedReturnType, Object worstCaseResult);

	
	/**
	 * This method will be called after all Plugins have been run.<br>
	 * The parameter plgCount will tell you, how many Plugins were executed.
	 */
	void updateCall(MethodType type, T anno, Map<Object, Object> prop, Object[] parameter, Object newValue, int plgCount);
}
