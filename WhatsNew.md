# Whats new? #

## Rev 64 ##

  * [Extension Annotation](WhatsNew#Extension_Annotation.md)
  * [InstallExt](WhatsNew#InstallExt.md)
  * [Update Module](WhatsNew#Update_Module.md)
  * [WhatsNew#Super\_Exception](WhatsNew#Super_Exception.md)

---


## Extension Annotation ##
If you'll write a Plugin or Module the associated annoatation has to annotate `PluginExtension` or `ModulExtension`. So JMapper can recognize the type of Extension by the annotation.

## InstallExt ##
Cause of the new annotations, I was able to write a new install-routine: `JMapper#installExt(Class<?> class)`. If you use this Methode, JMapper will look for implemented Interfaces and if there is a Module or Plugin, it will install it correctly.

## Update Module ##
Now you can update your Module after plugins had done their work. MapperModule got a new Method which will be called after all plugins were invoked. So e.g. the Linkmodule is able to use Plugins for Setter-Methods.

## Super Exception ##
If JMapper needs to throw an exception, it will be an exception inheriting JMapperException.