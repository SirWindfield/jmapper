package de.jfruit.factory.demo;

import java.util.Properties;

import javax.swing.JTextField;

import de.jfruit.jmapper.JMapper;
import de.jfruit.jmapper.link.ILinkParser;
import de.jfruit.jmapper.link.Link;

public class GameSettings 
{
	JTextField textField;
	Properties props=new Properties();
	MyConfig config;
	
	public static void main(String[] args) {
		new GameSettings();
	}
	
	public GameSettings() 
	{
		textField=new JTextField("800");		
		config=JMapper.createFromMap(props, MyConfig.class);
		
		config.setSample(5);
		System.out.println(config.getSample());
		
		config.setStoreWidth(textField);
		System.out.println("Width: " + config.getStoreWidth());
		
	}
}

interface MyConfig
{
	@Link(key="game_width",parser=JTFParser.class) 
	void setStoreWidth(JTextField field);
	
	@Link(key="game_width") 
	int getStoreWidth();
	
	@Link(key="sample")
	void setSample(int i);
	
	@Link(key="sample")
	int getSample();
}

class JTFParser 
	implements ILinkParser<Integer, JTextField>
{

	@Override
	public Integer defValue() {
		return -1;
	}

	@Override
	public Integer parse(JTextField tfield) {
		return Integer.parseInt(tfield.getText());
	}
}
