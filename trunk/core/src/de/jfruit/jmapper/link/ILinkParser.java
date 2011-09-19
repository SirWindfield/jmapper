package de.jfruit.jmapper.link;

public interface ILinkParser<T,S> 
{
	T defValue();
	T parse(S source);
}
