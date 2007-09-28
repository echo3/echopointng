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
import java.util.List;

/**
 * <code>CssClassDecl</code> holds parsed information about a declared
 * component class from a CSS Style Sheet.
 */
class CssClassDecl implements Cloneable {
	private String className = null;

	private Class componentClass;

	private String styleName = null;

	private String extendsStyleName = null;

	private int order = 0;

	private int lineNo;

	private List propertyList = new ArrayList();

	String getClassName() {
		return className;
	}

	int getLineNo() {
		return lineNo;
	}

	String getExtendsStyleName() {
		return extendsStyleName;
	}

	int getOrder() {
		return order;
	}

	String getStyleName() {
		return styleName;
	}

	void setExtendsStyleName(String baseStyleName) {
		this.extendsStyleName = baseStyleName;
	}

	void setClassName(String className) {
		this.className = className;
	}

	void setLineNo(int lineNo) {
		this.lineNo = lineNo;
	}

	void setOrder(int order) {
		this.order = order;
	}

	void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	void addProperty(CssClassPropertyDecl propertyEntry) {
		propertyEntry.setClassEntry(this);
		propertyList.add(propertyEntry);
	}

	CssClassPropertyDecl[] getProperties() {
		return (CssClassPropertyDecl[]) propertyList
				.toArray(new CssClassPropertyDecl[propertyList.size()]);
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	Class getComponentClass() {
		return componentClass;
	}

	void setComponentClass(Class classClass) {
		this.componentClass = classClass;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(className);
		if (styleName != null) {
			sb.append('#');
			sb.append(styleName);
		}
		if (extendsStyleName != null) {
			sb.append(" extends ");
			sb.append(extendsStyleName);
		}
		sb.append(" {\n");
		for (int i = 0; i < propertyList.size(); i++) {
			CssClassPropertyDecl attr = (CssClassPropertyDecl) propertyList.get(i);
			sb.append("\t");
			sb.append(attr.toString());
			sb.append(";\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
}