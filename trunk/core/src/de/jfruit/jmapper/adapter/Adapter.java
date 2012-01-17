package de.jfruit.jmapper.adapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.jfruit.jmapper.ext.ModuleExtension;

@ModuleExtension
@Target(value={ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter 
{	
	Class<? extends ILinkAdapter<?,?,?>> value();
}
