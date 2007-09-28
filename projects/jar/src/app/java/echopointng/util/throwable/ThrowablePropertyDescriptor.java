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

import java.io.Serializable;

/**
 * <code>ThrowablePropertyDescriptor</code> describes a property
 * of an Throwable
 */
public class ThrowablePropertyDescriptor implements Serializable {
	private Class type;
	private String name;
	private Object value;
	private int	modifiers;
	
	
	/**
	 * Constructs an empty <code>ThrowablePropertyDescriptor</code>
	 */
	ThrowablePropertyDescriptor() {
	}

	/**
	 * Constructs a <code>ThrowablePropertyDescriptor</code> with the name, type and value.
	 * 
	 * @param type  - the type of the Throwable property
	 * @param name  - the name of the Throwable property
	 * @param value - the value of the Throwable property
	 */	
	ThrowablePropertyDescriptor(Class type, String name, Object value, int modifiers) {
		this.type = type;
		this.name = name;
		this.value = value;
		this.modifiers = modifiers;
	}
	/** 
	 * Returns the name of the Throwable property
	 * @return the name of the Throwable property
	 */
	public String getName() {
		return name;
	}

	/** 
	 * Returns the modifiers of the Throwable property
	 * @return the modifiers of the Throwable property
	 */
	public int getModifiers() {
		return modifiers;
	}

	/** 
	 * Returns the type of the Throwable property
	 * @return the type of the Throwable property
	 */
	public Class getType() {
		return type;
	}

	/** 
	 * Returns the value of the Throwable property
	 * @return the value of the Throwable property
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns a trimmed String representation of the
	 * value of the Throwable property or null.
	 *  
	 * @return a trimmed String representation of the
	 * value of the Throwable property or null.
	 */
	public String getValueAsString() {
		if (value == null)
			return null;
		return value.toString();	
	}

	/** 
	 * Sets the type of the Throwable property
	 * @param type - the name of the Throwable property
	 */
	void setType(Class type) {
		this.type = type;
	}

	/** 
	 * Sets the name of the Throwable property
	 * @param name - the name of the Throwable property
	 */
	void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * Sets the modifiers of the Throwable property
	 * @param modifiers - the modifiers of the Throwable property
	 */
	void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}
	

	/** 
	 * Sets the value of the Throwable property
	 * @param value - the value of the Throwable property
	 */
	void setValue(Object value) {
		this.value = value;
	}

}
