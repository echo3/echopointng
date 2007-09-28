package echopointng.cutandpaste;
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

import echopointng.ComponentEx;
import echopointng.able.Delegateable;

import nextapp.echo2.app.Component;

/**
 * <code>AbstractComponentDelegate</code> is the abstract
 * base class for the other 'able delegate objects.
 */
public abstract class AbstractComponentDelegate implements  Delegateable, Serializable {
	private Component delegatee;

	/**
	 * Constructs a <code>ComponentDelegate</code> for a component
	 * 
	 * @param delegatee - the component upon whose behalf this
	 * 		delegate is working
	 */	
	public AbstractComponentDelegate(Component delegatee) {
		if (delegatee == null)
			throw new IllegalArgumentException("You must provide a delegate Component");
		this.delegatee = delegatee;
	}
	
	/**
	 * Returns the Component we are being a delegate for. 
	 * @return the Component we are being a delegate for.
	 */
	public Component getDelegatee() {
		return delegatee;
	}

	/**
	 * @see Component#getRenderProperty(String)
	 */
	public Object getRenderProperty(String propertyName) {
		return ComponentEx.getRenderProperty(delegatee,null);
	}
	
	/**
	 * @see Component#getRenderProperty(String, Object)
	 */
	public Object getRenderProperty(String propertyName, Object defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName);
	}
	
	/**
	 * Helper method to get 'boolean' properties, with a default value.
	 * 
	 * @return - the 'boolean' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected boolean getProperty(String propertyName, boolean defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'byte' properties, with a default value.
	 * 
	 * @return - the 'byte' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected byte getProperty(String propertyName, byte defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'char' properties, with a default value.
	 * 
	 * @return - the 'char' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected char getProperty(String propertyName, char defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'double' properties, with a default value.
	 * 
	 * @return - the 'double' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected double getProperty(String propertyName, double defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'float' properties, with a default value.
	 * 
	 * @return - the 'float' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected float getProperty(String propertyName, float defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'int' properties, with a default value.
	 * 
	 * @return - the 'int' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected int getProperty(String propertyName, int defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'long' properties, with a default value.
	 * 
	 * @return - the 'long' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected long getProperty(String propertyName, long defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'short' properties, with a default value.
	 * 
	 * @return - the 'short' property or the default value if its null
	 * @see Component#getProperty(String)
	 */
	protected short getProperty(String propertyName, short defaultValue) {
		return ComponentEx.getProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'boolean' render properties, with a default value.
	 * 
	 * @return - the 'boolean' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected  boolean getRenderProperty(String propertyName, boolean defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'byte' render properties, with a default value.
	 * 
	 * @return - the 'byte' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected byte getRenderProperty(String propertyName, byte defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'char' render properties, with a default value.
	 * 
	 * @return - the 'char' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected char getRenderProperty(String propertyName, char defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'double' render properties, with a default value.
	 * 
	 * @return - the 'double' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected double getRenderProperty(String propertyName, double defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'float' render properties, with a default value.
	 * 
	 * @return - the 'float' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected float getRenderProperty(String propertyName, float defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'int' render properties, with a default value.
	 * 
	 * @return - the 'int' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected int getRenderProperty(String propertyName, int defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'long' render properties, with a default value.
	 * 
	 * @return - the 'long' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected long getRenderProperty(String propertyName, long defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to get 'short' render properties, with a default value.
	 * 
	 * @return - the 'short' render property or the default value if its null
	 * @see Component#getRenderProperty(String, Object)
	 */
	protected short getRenderProperty(String propertyName, short defaultValue) {
		return ComponentEx.getRenderProperty(delegatee,propertyName,defaultValue);
	}

	/**
	 * Helper method to set 'boolean' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, boolean newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Boolean(newValue));
	}

	/**
	 * Helper method to set 'byte' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, byte newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Byte(newValue));
	}

	/**
	 * Helper method to set 'char' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, char newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Character(newValue));
	}

	/**
	 * Helper method to set 'double' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, double newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Double(newValue));
	}

	/**
	 * Helper method to set 'float' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, float newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Float(newValue));
	}

	/**
	 * Helper method to set 'int' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, int newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Integer(newValue));
	}

	/**
	 * Helper method to set 'long' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, long newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Long(newValue));
	}

	/**
	 * Helper method to set 'short' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, short newValue) {
		ComponentEx.setProperty(delegatee,propertyName,new Short(newValue));
	}

	/**
	 * Helper method to get 'Object' properties.
	 * 
	 * @return - the 'Object' property.
	 * 
	 * @see Component#getProperty(String)
	 */
	protected Object getProperty(String propertyName) {
		return ComponentEx.getProperty(delegatee,propertyName);
	}


	/**
	 * Helper method to set 'Object' property values
	 * 
	 * @see Component#setProperty(String, Object)
	 */
	protected void setProperty(String propertyName, Object newValue) {
		ComponentEx.setProperty(delegatee,propertyName,newValue);
	}
}
