package echopointng.util.throwable;
/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import echopointng.util.reflect.ReflectionKit;

/**
 * <code>ThrowableKit</code> provides helper methods for handling
 * Exceptions in Echo web applications.  It is expecially useful
 * for allowing JDK 1.4 exception chaining while still retaining
 * a JDK 1.3 compliant code base. 
 */
public class ThrowableKit {


	/** not instantiable */
	private ThrowableKit() {
	}
	
	/**
	 * This method creates a Throwable derived object that will attach 
	 * the root cause if you are running under JDK 1.4 or later.  
	 * <p>
	 * This method makes it easier in dumps that find the root cause of a problem.  
	 * <p>
	 * If the contructor(String,Throwable) is not available (JDK 1.3 or below)
	 * then the message + " : " + cause.toString() will be used and the root
	 * cause Throwable stack trace will not be avalable.
	 * <p>
	 * This method allows you to use the new 1.4 exception chaining if possible. 
	 *
	 * @param throwableClass  - must be a class derived from Throwable.class  
	 * @param message - a custom error message, can be null.
	 * @param cause - the root cause Throwable
	 * @return a new RuntimeException
	 */
	public static Throwable makeThrowable(Class throwableClass, String message, Throwable cause) {
		if (throwableClass == null || ! (Throwable.class.isAssignableFrom(throwableClass)))
			// for gods sake get it right will you
			throw new IllegalArgumentException("The class you provided :" + throwableClass + " is not derived from Throwable.class");
			
		// we need to use relection to find out if we have the
		// RuntimeException(String,Throwable) contructor available
		try {
			Constructor c = throwableClass.getDeclaredConstructor(new Class[] { String.class,Throwable.class});
			//
			// if we have that contructor then we must have the get/setStackTrace method as well
			Object t = c.newInstance(new Object[] { message, cause });
			return (Throwable) t;
		} catch (Exception e) {
			// no good.  Must be JDK 1.4
		}
		
		try {
			String messageText;
			// no good, just make it into a message : cause.toString()
			if (message == null)
				messageText = cause.toString();
			else
				messageText = message + " : " + cause.toString();
			
			Constructor c = throwableClass.getDeclaredConstructor(new Class[] {String.class});
			Object t = c.newInstance(new Object[] { messageText });
			return (Throwable) t;
		} catch (Exception e) {
			throw new IllegalStateException("A constructor(String) was not available for class :" + throwableClass);
		}
	}
	
	/**
	 *  Short hand method for ThrowableKit.makeThrowable(throwableClass, null,cause);
	 * 
	 * @see ThrowableKit#makeThrowable(Class, String, Throwable)
	 */
	public static Throwable makeThrowable(Class throwableClass, Throwable cause) {
		return makeThrowable(throwableClass,null,cause);
	}
	
	/**
	 * This method creates a RuntimeException that will attach the root cause
	 * if you are running under JDK 1.4 or later.  This makes it easier in 
	 * Exception dumps that find the root cause of a problem.  If the
	 * RuntimeException(String,Throwable) contructor is not available (JDK 1.3 or below)
	 * then the message + " : " + cause.toString() will be used and the root
	 * cause Throwable stack trace will not be avalable.
	 * <p>
	 * This method allows you to use the new 1.4 exception chaining if possible. 
	 *  
	 * @param message - a custom error message, can be null.
	 * @param cause - the root cause Throwable
	 * @return a new RuntimeException
	 */
	public static RuntimeException makeRuntimeException(String message, Throwable cause) {
		return (RuntimeException) makeThrowable(RuntimeException.class,message,cause);
	}

	/**
	 * A short cut method for ThrowableKit.makeRuntimeException(null,cause);
	 * 
	 * @see ThrowableKit#makeRuntimeException(String, Throwable)
	 */
	public static RuntimeException makeRuntimeException(Throwable cause) {
		return makeRuntimeException(null,cause);
	}
	
	/**
	 * This method attempts to attach the cause Throwable to the provided
	 * Throwable.  It emuluates the Throwable.initCause(Throwable) method
	 * found in JDK 1.4.
	 * <p>
	 * If this cant be done then nothing is attached, such as when you are 
	 * running under JDK 1.3 
	 * <p>
     * <p>This method can be called at most once on an Throwable.  It is generally called from 
     * within the constructor, or immediately after creating the
     * throwable.  If this throwable was created with {@link Throwable#Throwable(Throwable)} or
     * {@link Throwable#Throwable(String,Throwable)}, this method has no effect since
     * the cause has already been set.
	 *  
	 * @param throwable the target Throwable
	 * @param cause the root cause Throwablew to attach
	 * @return the target Throwable t
	 * 
	 */
	public static Throwable attachCause(Throwable throwable, Throwable cause) {
		 try {
			Method m = Throwable.class.getDeclaredMethod("initCause",new Class[] { Throwable.class } );
			throwable = (Throwable) m.invoke(throwable,new Object[] { cause });
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalStateException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		// if we got here then we could not init the Throwable.  Ohh well we tried
		return throwable;
	}

	/**
	 * Short hand method for ThrowableKit.attachCause(throwable, cause);
	 * 
	 * @see ThrowableKit#attachCause(Throwable, Throwable)
	 * @see Throwable#initCause(java.lang.Throwable)
	 */
	public static Throwable initCause(Throwable throwable, Throwable cause) {
		return attachCause(throwable,cause);
	}
	
	
	/**
	 * This method will examine a Throwable and return a 
	 * ThrowableDescriptor that can then be used to 
	 * output error details.
	 * <p>
	 * This works equally well on JDK 1.3 as it does on JDK 1.4.
	 * <p>
	 * Any circular references in the Exception chaining
	 * will be removed.
	 *  
	 * @param throwable - the Throwable to examine
	 * @return a ThrowableDescriptor that describes the Throwable
	 */
	public static ThrowableDescriptor describeThrowable(Throwable throwable) {
		
		// we keep track of throwables we have already seen so as
		// we dont get into q circular reference loop
		Map seenThrowables = new HashMap();
		
		ThrowableDescriptor descriptor = new ThrowableDescriptor(throwable);
		seenThrowables.put(throwable,throwable);
		
		examineThrowable(descriptor, throwable, seenThrowables);
		
		return descriptor;
	}
	
	/*
	 * Examines a throwable, finding all its child properties as well
	 * as root causes as well as filling the stack trace
	 */
	private static void examineThrowable(ThrowableDescriptor descriptor, Throwable throwable, Map seenThrowables) {
		Method[] properties = ReflectionKit.getAllDeclaredMethods(throwable.getClass());
		List propertyList = new ArrayList(properties.length);
		List causeList = new ArrayList(properties.length);
		String throwableMessage = throwable.getMessage();
		
		for (int i = 0; i < properties.length; i++) {
			Method getter =  properties[i];
			if (! ReflectionKit.isGetter(getter))
				continue;
			
			String name = null;
			Class type = null;	
			Object value = null;
			int modifiers = getter.getModifiers();
			try {
				//
				// try and get access to non public methods	
				getter.setAccessible(true);
				value = getter.invoke(throwable,null);
				type = getter.getReturnType();
				name =  ReflectionKit.decapitalize(getter.getName());
				if (name.equals("class") || 
					name.equals("message") || 
					name.equals("ourStackTrace") || 
					name.equals("stackTraceDepth") || 
					name.equals("stackTrace")) {
						continue;
					}
			} catch (IllegalArgumentException e1) {
				continue;
			} catch (SecurityException e1) {
				continue;
			} catch (IllegalAccessException e1) {
				continue;
			} catch (InvocationTargetException e1) {
				continue;
			}
			if (Throwable.class.isAssignableFrom(type)) {
				if (value == null)
					continue;
					
				// its a Throwable, so lets examine it
				Throwable child = (Throwable) value;
				//
				// if we have seen it before we dont want a circular
				// reference and hence an infinite sized tree graph 
				//
				if (seenThrowables.containsKey(child))
					continue;
				seenThrowables.put(child,child);
				
				ThrowableDescriptor childDesc = new ThrowableDescriptor(child);
				examineThrowable(childDesc,child,seenThrowables);
				
				causeList.add(childDesc);	
			} else {
				if (value == throwableMessage)
					continue;
				ThrowablePropertyDescriptor propertyDesc = new ThrowablePropertyDescriptor(type,name,value,modifiers);
				propertyList.add(propertyDesc);
			}
			
		}
		descriptor.setCauses((ThrowableDescriptor[]) causeList.toArray(new ThrowableDescriptor[causeList.size()]));
		descriptor.setProperties((ThrowablePropertyDescriptor[]) propertyList.toArray(new ThrowablePropertyDescriptor[propertyList.size()]));
		descriptor.setStackTrace(examineStackTrace(throwable));
		
	}
	
	/*
	 * Examines the stack trace of the Throwable and converts it
	 * into an array of Strings.
	 */
	private static String[] examineStackTrace(Throwable parentThrowable) {
		CharArrayWriter caw = new CharArrayWriter();
		PrintWriter pw = new PrintWriter(caw);
		parentThrowable.printStackTrace(pw);
		
		List stackTraceList = new ArrayList();
		StringReader sr = new StringReader(caw.toString());
		BufferedReader reader = new BufferedReader(sr);
		boolean atStart = true;
		while (true) {
			String line = null;
			try {
				line = reader.readLine();
			} catch (IOException e) {
			}
			if (line == null)
				break;
			if (line.startsWith("Caused by"))
				break;
			// skip the non stack frame bit at the start			
			if (atStart) {
				atStart = false;
				continue;
			}
			stackTraceList.add(line);
		}
		return (String[]) stackTraceList.toArray(new String[stackTraceList.size()]);
	}
	
	
	/**
	 * Prints a textual description of a Throwable and its properties,
	 * causes and stack traces.
	 * 
	 * @param throwable - the Throwable in question
	 * @param out - the PrintStream to write to
	 * @param indent - control whether tab indents will be used 
	 */
	public static void printThrowableDescription(Throwable throwable, PrintStream out, boolean indent) {
		if (throwable == null || out == null)
			return;
		ThrowableDescriptor desc = describeThrowable(throwable);
		printThrowableDescription0(desc,out,-1,indent);
	}

	/**
	 * Shorthand method for ThrowableKit.printThrowableDescription(throwable,out,true);
	 * 
	 * @see ThrowableKit#printThrowableDescription(Throwable, PrintStream, boolean)
	 */
	public static void printThrowableDescription(Throwable throwable, PrintStream out) {
		printThrowableDescription(throwable,out,true);
	}
	
	/*
	 * Does the printing of the Throwable details
	 */	
	private static void printThrowableDescription0(ThrowableDescriptor desc, PrintStream out, int tabLevel, boolean indent) {
		if (indent)
			tabLevel++;

		boolean doneHeader = false;		
		printTabs(out,tabLevel);
		out.print(desc.getType().getName());
		out.print(" : ");
		out.print(desc.getMessage());
		out.println();
		
		ThrowablePropertyDescriptor properties[] = desc.getProperties();
		for (int i = 0; i < properties.length; i++) {
			if (! doneHeader) {
				doneHeader = true;
				printTabs(out,tabLevel);
				out.println("Properties : ");
			}
			
			printTabs(out,tabLevel);
			out.print('\t');
			out.print(properties[i].getName());
			out.print(" : ");
			out.print(properties[i].getValueAsString());
			out.print(" (");
			out.print(properties[i].getType().getName());
			out.print(" ");
			String modifiers = Modifier.toString(properties[i].getModifiers()); 
			out.print(modifiers == null || modifiers.length() == 0 ? "default" : modifiers);
			out.print(")");
			out.println();
		}

		doneHeader = false;
		String[] stackTrace = desc.getStackTrace();
		for (int i = 0; i < stackTrace.length; i++) {
			if (! doneHeader) {
				doneHeader = true;
				printTabs(out,tabLevel);
				out.println("Stack Trace : ");
			}
			printTabs(out,tabLevel);
			out.println(stackTrace[i]);
		}
		
		doneHeader = false;
		ThrowableDescriptor causes[] = desc.getCauses();
		for (int i = 0; i < causes.length; i++) {
			if (! doneHeader) {
				doneHeader = true;
				printTabs(out,tabLevel);
				out.println("Caused By : ");
			}
			printThrowableDescription0(causes[i],out,tabLevel,indent);
		} 	
	}

	/*
	 * Print tabs! 
	 */
	private static void printTabs(PrintStream out, int tabLevel) {
		for (int i = 0; i < tabLevel; i++)
			out.print("\t");
	}
}
	