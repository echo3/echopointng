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

import echopointng.able.AccessKeyable;

import nextapp.echo2.app.Component;

/**
 * <code>AccessKeyableDelegate</code> is a simple delegate object
 * than can be used inside a component that implements
 * AccessKeyable.
 */
public class AccessKeyableDelegate extends AbstractComponentDelegate implements AccessKeyable, Serializable {

    
	public AccessKeyableDelegate(Component delegatee) {
		super(delegatee);
	}
	
	/**
	 * @see echopointng.able.AccessKeyable#getAccessKey()
	 */
	public String getAccessKey() {
		return (String) getProperty(PROPERTY_ACCESS_KEY);
	}

	/**
	 * @see echopointng.able.AccessKeyable#setAccessKey(java.lang.String)
	 */
	public void setAccessKey(String newValue) {
		setProperty(PROPERTY_ACCESS_KEY,newValue);
	}
}
