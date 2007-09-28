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

/**
 * <code>ThrowableDescriptor</code> describes an Throwable
 * and lists if properties, causes and its stack trace.
 * 
 */
public class ThrowableDescriptor extends ThrowablePropertyDescriptor {

	private Throwable				throwable;
	private String					message;
	private ThrowableDescriptor[] 	causes;
	private ThrowablePropertyDescriptor[] properties;
	private String[]				stackTrace;
	
	ThrowableDescriptor(Throwable throwable) {
		setThrowable(throwable);
		setMessage(throwable.getLocalizedMessage());
		setType(throwable.getClass());
		setValue(this.message);
		setName(throwable.getClass().getName());
		
		setCauses(new ThrowableDescriptor[0]);
		setStackTrace(new String[0]); 
		setProperties(new ThrowablePropertyDescriptor[0]);
		setModifiers(throwable.getClass().getModifiers());
	}
	
	
	/**
	 * Returns the causes of the Throwable or a 0 length
	 * array if they are not known.
	 * 
	 * @return the causes of the Throwable or a 0 length
	 * array if they are not known.
	 */
	public ThrowableDescriptor[] getCauses() {
		return causes;
	}

	/** 
	 * Returns the localised message of the Throwable
	 * @return the localised message of the Throwable
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns an array of stack tace messages for the Throwable. 
	 * @return an array of stack tace messages for the Throwable.
	 */
	public String[] getStackTrace() {
		return stackTrace;
	}

	/** 
	 * Returns the actual Throwable that this ThrowableDescriptor describes.
	 * @return the actual Throwable that this ThrowableDescriptor describes.
	 */
	public Throwable getThrowable() {
		return throwable;
	}

	/** 
	 * Returns an array of properties of the Throwable.
	 * @return an array of properties of the Throwable.
	 */
	public ThrowablePropertyDescriptor[] getProperties() {
		return properties;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new  StringBuffer();
		sb.append(getType().getName());
		sb.append(':');
		sb.append(message);
		for (int i = 0; i < properties.length; i++) {
			sb.append(' ');
			sb.append(properties[i].getName());
			sb.append(':');
			sb.append(properties[i].getValueAsString());
		}
		for (int i = 0; i < causes.length; i++) {
			sb.append(' ');
			sb.append(causes[i].getName());
			sb.append(':');
			sb.append(causes[i].getValueAsString());
		}
		return sb.toString();
	}

	void setCauses(ThrowableDescriptor[] descriptors) {
		causes = descriptors;
	}

	void setMessage(String string) {
		message = string;
	}

	void setProperties(ThrowablePropertyDescriptor[] descriptors) {
		properties = descriptors;
	}

	public void setStackTrace(String[] strings) {
		stackTrace = strings;
	}

	void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

}
