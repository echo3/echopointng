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
package echopointng;

import java.util.Iterator;

import nextapp.echo2.app.MutableStyle;
import nextapp.echo2.app.Style;

/** 
 * <code>MutableStyleEx</code> is a <code>MutableStyle</code> with more setters
 * for the basic Java types.  Its allows for <code>setProperty(PROP_NAME,true)</code> instead of
 * <code>setProperty(PROP_NAME,Boolean.valueOf(true))</code>, which I believe is a lot more
 * natural.
 */
public class MutableStyleEx extends MutableStyle {

	/**
	 * Constructs an empty <code>MutableStyleEx</code>
	 */
	public MutableStyleEx() {
		super();
	}
	
	/**
	 * Constructs a <code>MutableStyleEx</code> based on the values of a another <code>Style</code>. 
	 * <p> 
	 * Unlike a <code>DerivedMutableStyle</code>, this makes a direct copy of the <code>otherStyle</code> and
	 * sets those values into this <code>MutableStyleEx</code>
	 *
	 * @param otherStyle - the other <code>Style</code> to copy
	 */
	public MutableStyleEx(Style otherStyle) {
		super();
		for (Iterator iter = otherStyle.getPropertyNames(); iter.hasNext();) {
			String propertyName = (String) iter.next();
			if (otherStyle.isPropertySet(propertyName)) {
				boolean indexSet = false;
				for (Iterator iter2 = otherStyle.getPropertyIndices(propertyName); iter2 != null && iter2.hasNext();) {
					int propertyIndex = ((Integer) iter2	.next()).intValue();
					this.setIndexedProperty(propertyName, propertyIndex, otherStyle.getIndexedProperty(propertyName, propertyIndex));
					indexSet = true;
				} 
				if (! indexSet) {
					this.setProperty(propertyName,otherStyle.getProperty(propertyName));
				}
			}
		} 
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, byte propertyValue) {
		super.setProperty(propertyName, new Byte(propertyValue));
	}
	
	
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, short propertyValue) {
		super.setProperty(propertyName, new Short(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, int propertyValue) {
		super.setProperty(propertyName, new Integer(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, long propertyValue) {
		super.setProperty(propertyName, new Long(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, float propertyValue) {
		super.setProperty(propertyName, new Float(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, double propertyValue) {
		super.setProperty(propertyName, new Double(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, char propertyValue) {
		super.setProperty(propertyName, new Character(propertyValue));
	}
	/**
	 * @see nextapp.echo2.app.MutableStyle#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, boolean propertyValue) {
		super.setProperty(propertyName, Boolean.valueOf(propertyValue));
	}
}
