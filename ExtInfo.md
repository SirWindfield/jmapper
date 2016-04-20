# Extensions #
  * [How do I install an extension?](ExtInfo#How_do_I_install_an_extension?.md)
  * [Differences between  plugin and module](ExtInfo#Differences_between_plugin_and_module.md)


---


## How do I install an extension? ##
JMapper provides several methods for installing a new extension(plugin or module). An extension requires an annotation and this annotation has to be annotated with `@Retention(RetentionPolicy.RUNTIME)`. This annotion is required, because the factory needs to know, when the plugin should be used.

The easiest way, to install your extension is via the method: `JMapper#install(Class,Class)`. You can only use this method, if the given extension has a default constructor with no arguments. Otherwise, you have to use `JMapper#install(Class,MapperModule)` or `JMapper#install(Class,MapperPlugin)`.

**Important**: if you install an extension, it will also work on objects (created by the JMapperfactory) which still exists! Be carefull, I am not sure, if I will keep this "feature"! So better first intall the required extensions and then use the factory.


---


## Differences between  plugin and module ##
Plugins and moduls are not equal! On a method can run several plugins but at least only **one** module!