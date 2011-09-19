package de.jfruit.factory.demo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.jfruit.jmapper.JMapper;
import de.jfruit.jmapper.MethodType;
import de.jfruit.jmapper.ext.MapperPlugin;
import de.jfruit.jmapper.link.Link;

public class BindingTest 
{
	private static JTextField text=new JTextField();	
	
	public static void main(String[] args) 
	{
		JMapper.install(TextBinding.class, MyTextPlugin.class);
		
		Properties props=new Properties();
		props.put("name","hans");		

		Conf config=JMapper.createFromMap(props, Conf.class);
		
		final JFrame frame=new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		text.setText(config.getName());
		
		frame.add(text);
		frame.pack();
		frame.setLocationRelativeTo(null);
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
		
		String neu = JOptionPane.showInputDialog("Neuer wert");
		
		if(neu==null) 
			neu="";
		
		config.setName(neu);
	}
	
	public interface Conf
	{		
		@Link(key="name")		
		String getName();
	
		@TextBinding 
		@Link(key="name")
		void setName(String name);
	}
	
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface TextBinding {}
	
	public static class MyTextPlugin implements MapperPlugin<TextBinding>
	{
		@Override
		public Object executePlugin(MethodType type, TextBinding anno,
				Map<Object, Object> prop, Object[] parameter,
				Object currentValue, Object worstCaseResult) {

			if(type==MethodType.SETTER) {
				text.setText(String.valueOf(currentValue));
			}
			
			return worstCaseResult;
		}
	}
}
