package de.jfruit.jmapper;

import java.lang.annotation.Annotation;


public final class JMapperExceptionHandler 
{
	public static class JMapperException extends RuntimeException 
	{
		private static final long serialVersionUID = 1L;

		public JMapperException() {
			super();
		}

		public JMapperException(final String message, final Throwable cause) {
			super(message, cause);
		}

		public JMapperException(final String message) {
			super(message);
		}

		public JMapperException(final Throwable cause) {
			super(cause);
		}
		
	}
	
	public static class InvalidAnnotation extends JMapperException
	{
		private static final long serialVersionUID = 1L;

		public InvalidAnnotation(final Class<? extends Annotation> extension, final Class<? extends Annotation> expected) 
		{
			super("Annotation "+extension+" is not annotated with "+expected);
		}
	}
}
