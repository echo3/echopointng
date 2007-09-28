package echopointng.stylesheet;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <code>CssClassPropertyDecl</code> holds a declared CSS property entry. 
 * <p>
 * This is usually a single string value however it can be an 
 * array of CssClassPropertyDecl objects (sub properties), which
 * allows for creating trees of nested objects.
 */
class CssClassPropertyDecl implements Cloneable {
	private int lineNo;

	private CssClassDecl classEntry;

	private String propertyName;

	private String targetClassName;

	private int index = -1;

	// can contain a single String or a series of AttrEntry
	private List propertyValues = new ArrayList();

	boolean isSingleValue() {
		if (propertyValues.size() == 1) {
			return propertyValues.get(0) instanceof String;
		}
		return false;
	}

	void setClassEntry(CssClassDecl classEntry) {
		this.classEntry = classEntry;
	}

	String getPropertyValue() {
		if (isSingleValue())
			return (String) propertyValues.get(0);
		return null;
	}

	CssClassPropertyDecl[] getProperties() {
		return (CssClassPropertyDecl[]) propertyValues
				.toArray(new CssClassPropertyDecl[propertyValues.size()]);
	}

	void setPropertyValue(String value) {
		propertyValues.clear();
		propertyValues.add(value);
	}

	void addProperty(CssClassPropertyDecl propertyEntry) {
		propertyValues.add(propertyEntry);
	}

	CssClassDecl getClassEntry() {
		return classEntry;
	}

	boolean isIndexedProperty() {
		return index >= 0;
	}

	int getIndex() {
		return index;
	}

	void setIndex(int index) {
		this.index = index;
	}

	int getLineNo() {
		return lineNo;
	}

	String getPropertyName() {
		return propertyName;
	}

	void setPropertyName(String propertyName) {
		//
		// we need to fix up indexed property names here
		int opener = propertyName.indexOf('[');
		int ender = propertyName.indexOf(']');
		if (opener > 0 && ender == propertyName.length() - 1) {
			// fix up needed
			this.propertyName = propertyName.substring(0, opener);
			String indexNum = propertyName.substring(opener + 1, ender);
			try {
				this.index = Integer.parseInt(indexNum);
			} catch (Exception e) {
			}
		} else {
			this.propertyName = propertyName;
		}
	}

	String getTargetClassName() {
		return targetClassName;
	}

	void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(propertyName);
		if (targetClassName != null) {
			sb.append(" targetClass:");
			sb.append(targetClassName);
		}
		sb.append(" : ");
		if (isSingleValue()) {
			sb.append(getPropertyValue());
		} else {
			sb.append(" {\n\t");
			for (Iterator iter = propertyValues.iterator(); iter.hasNext();) {
				CssClassPropertyDecl element = (CssClassPropertyDecl) iter.next();
				sb.append(element);
				if (iter.hasNext())
					sb.append(" , ");
			}
			sb.append("}\n");
		}
		return sb.toString();
	}
}