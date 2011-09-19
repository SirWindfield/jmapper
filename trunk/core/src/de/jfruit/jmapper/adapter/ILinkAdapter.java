package de.jfruit.jmapper.adapter;

public interface ILinkAdapter<K,T,S> 
{
	K key();
	T defValue();
	T parse(S source);
}
