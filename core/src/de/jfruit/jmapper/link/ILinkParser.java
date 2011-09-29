package de.jfruit.jmapper.link;

public interface ILinkParser<Result,ParameterType> 
{
	Result defValue();
	Result parse(ParameterType source);
}
