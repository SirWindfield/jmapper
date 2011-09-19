package de.jfruit.jmapper;

import de.jfruit.jmapper.link.ILinkParser;

public class NullParser	implements ILinkParser<Object,Object>
{

	@Override
	public Object defValue() {
		return null;
	}

	@Override
	public Object parse(Object o) {
		return null;
	}
}
