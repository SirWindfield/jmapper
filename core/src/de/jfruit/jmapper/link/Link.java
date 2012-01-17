package de.jfruit.jmapper.link;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import de.jfruit.jmapper.NullParser;
import de.jfruit.jmapper.ext.ModuleExtension;

@ModuleExtension
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Link
{
	String key();	
	Class<? extends ILinkParser<?,?>> parser() default NullParser.class;
	boolean plugins() default true;
}
