# Getting Started #

Welcome to the Getting Started. I will show you the basics of JMapper and how to use them. Here is the table of contents:

  * [Using @Link](GettingStarted#Using_@link.md)
  * [Grant write permission](GettingStarted#Grant_write_permission.md)
  * [Parser](GettingStarted#Parser.md)


---


## Using @Link ##
The simplest way to create bindings between your interface and your map is to use Link(s). Link is an annotation having 2 parameters:

`String key [required]` each entry of your map has a key. This key is required for the binding. If the map does not contain this key, the method will return `null` or `0`

`Class<? extends ILinkParser<>> [optional]` this parameter will be explained later in the Getting Started.

Now we will define an interface (read-only) which will be linked to a map containing several applicationdata:

```
public interface IConfiguration 
{	
	@Link(key="appName")
	String getAppName();	
	
	@Link(key="major")
	int getMajorVersionNumber();
	
	@Link(key="minor")
	int getMinorVersionNumber();
	
	@Link(key="mikro")
	int getMikroVersionNumber();
}
```

If you've read the information above, there should no questions be left at this point. So I will continue with a simple main:

```
public class GettingStarted 
{
	public static void main(String[] args) 
	{
		Properties props=new Properties();		
		IConfiguration config=JMapper.createFromMap(props, IConfiguration.class);
		System.out.println("AppName: " + config.getAppName());
	}
}
```

As you can see, we have a map (Properties) and JMapper will define the interface for us. But our interface contains no data, so `System.out.println("AppName: " + config.getAppName());`
will print

`AppName: null`

to the console. If you want to get a result containing the application name, you have to store a value in the map, with the key: appName:
```
Properties props=new Properties();
// Storing some data in the map
props.setProperty("appName", "name of the Application");
IConfiguration config=JMapper.createFromMap(props, IConfiguration.class);
System.out.println("AppName: " + config.getAppName());
```

Run the application again and you can read:

`AppName: name of the Application`

in your console.


---


## Grant write permission ##

Reading seems to be no problem, but now we'd like to have an interface that can write to the map. We could add some methods to our IConfiguration but we also could create a new one. Lets call it IWritableConfiguration and this interface will extend IConfiguration:

```
public interface IWritableConfiguration extends IConfiguration
{
	@Link(key="appName")
	void setAppName(String title);
}
```

At this point, you should have noticed, that neither our getter nor our setter has any flag like TYPE.GETTER or TYPE.SETTER. JMapper recognize what type of bean your method is. JMapper at once checks if the returntype is void. In this case JMapper will handle the method like a setter. If not, JMapper looks if the method starts with "set"<sub>(true->setter|false->getter)</sub>. Well, you better use java beans or JMapper may will handle it wrong.

I've modified our main-method for testing our changes:
```
Properties props = new Properties();
props.setProperty("appName", "name of the Application");
IConfiguration config = JMapper.createFromMap(props,IConfiguration.class);
System.out.println("AppName: " + config.getAppName());

// New!!		
IWritableConfiguration wconfig=JMapper.createFromMap(props, IWritableConfiguration.class);
wconfig.setAppName("New Title");

System.out.println("From wconfig: " + wconfig.getAppName());
System.out.println("From config : " + config.getAppName());
```

Result:
```
AppName: name of the Application
From wconfig: New Title
From config : New Title
```

JMapper also has linked the extended interface correctly. This example also shows you: the interfaces have live-bindings to the map, because the changes are immediately accessable through our read-only interface.


---


## Parser ##

Above I told you: It seems reading is no problem. The point is: it only seems there would be no problem. Lets try out following:

```
Properties props = new Properties();
props.setProperty("minor", "42");		
IConfiguration config = JMapper.createFromMap(props,IConfiguration.class);
System.out.println("Minor-version: " + config.getMinorVersionNumber());
```

If you now have expected a result like this:
```
Minor-version: 42
```
then I have bad news for you. The result is:
```
Minor-version: 0
```

But why? To get this, we have to look at our method:
```
@Link(key="minor")
int getMinorVersionNumber();
```
JMapper expects an Integer as returntype but got a String. To get JMapper work with the String you have to use a parser. At once we need a new class implementing ILinkParser with a default constructor(!important!):
```
public class StringToIntegerParser implements ILinkParser<Integer,String>
{
	@Override
	public Integer defValue() {
		return 0;
	}

	@Override
	public Integer parse(String source) {
		return Integer.valueOf(source);
	}
	
}
```

Now we have to tell JMapper, that it should use this class to parse our String to an Integer. You can do this by using the second parameter of your Link annotation:
```
@Link(key="minor", parser=StringToIntegerParser.class)
int getMinorVersionNumber();
```

Now re-run your main without any changes and you will get your expected result: 42!

Of course you could have stored the 42 like this:
```
props.put("minor", 42);
```
without getting any trouble.