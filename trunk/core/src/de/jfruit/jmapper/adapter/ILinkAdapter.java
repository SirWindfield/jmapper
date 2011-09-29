package de.jfruit.jmapper.adapter;

public interface ILinkAdapter<Key,Result,ParameterType>
{
	Key key();
	Result defValue();
	Result parse(ParameterType source);
}
