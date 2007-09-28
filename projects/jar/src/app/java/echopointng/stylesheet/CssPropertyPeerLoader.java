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

import java.util.Map;
import java.util.WeakHashMap;

import nextapp.echo2.app.util.PeerFactory;

/**
 * <code>CssPropertyPeerLoader</code> finds and caches appropriate
 * <code>CssPropertyPeer</code> instances for a given property class. This
 * allows String values to be converted into Java object instances and vice
 * versa.
 * 
 * @see echopointng.stylesheet.CssPropertyPeer
 */
public class CssPropertyPeerLoader {

	private static final String PROPERTY_PEERS_PATH = "META-INF/echopointng/stylesheet/CssPropertyPeers.properties";

	private static final Map classLoaderToPropertyLoaderMap = new WeakHashMap();

	/**
	 * Creates or retrieves a <code>CssPropertyPeerLoader</code>.
	 * 
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for dynamically loading
	 *            property classes
	 * @return the <code>CssPropertyPeerLoader</code>
	 */
	public static CssPropertyPeerLoader forClassLoader(ClassLoader classLoader) {
		synchronized (classLoaderToPropertyLoaderMap) {
			CssPropertyPeerLoader propertyLoader = (CssPropertyPeerLoader) classLoaderToPropertyLoaderMap.get(classLoader);
			if (propertyLoader == null) {
				propertyLoader = new CssPropertyPeerLoader(classLoader);
				classLoaderToPropertyLoaderMap.put(classLoader, propertyLoader);
			}
			return propertyLoader;
		}
	}

	private PeerFactory propertyPeerFactory;

	/**
	 * Creates a new <code>CssPropertyPeerLoader</code>.
	 * 
	 * @param classLoader
	 *            the <code>ClassLoader</code> to use for dynamically loading
	 *            property classes
	 */
	private CssPropertyPeerLoader(ClassLoader classLoader) {
		super();
		propertyPeerFactory = new PeerFactory(PROPERTY_PEERS_PATH, classLoader);
	}

	/**
	 * Retrieves a Java object value from an CSS property string.
	 * 
	 * @param classLoader -
	 *            the class loader in use
	 * @param componentClass -
	 *            the object containing the property
	 * @param propertyClass -
	 *            the class of the property
	 * @param propertyValue -
	 *            the property value to analyze
	 * @return the property value
	 * @throws InvalidPropertyException
	 */
	public Object getObject(ClassLoader classLoader, Class componentClass, Class propertyClass, String propertyValue, int lineNo)
			throws CssInvalidValueException {
		CssPropertyPeer propertyPeer = getPropertyPeer(propertyClass);
		if (propertyPeer == null) {
			throw new CssInvalidValueException("Peer not found for property class: " + propertyClass, null, lineNo);
		}
		return propertyPeer.getJavaObject(classLoader, componentClass, propertyValue, lineNo);
	}

	/**
	 * Retrieves a CSS property value string for a given Java object
	 * 
	 * @param classLoader -
	 *            the class loader in use
	 * @param componentClass
	 *            the object containing the property
	 * @param propertyClass -
	 *            the class of the property
	 * @param propertyValue -
	 *            the property object value to analyze
	 * @return the property value as a string
	 * @throws InvalidPropertyException
	 */
	public String getString(ClassLoader classLoader, Class componentClass, Class propertyClass, Object propertyValue) throws CssInvalidValueException {
		CssPropertyPeer propertyPeer = getPropertyPeer(propertyClass);
		if (propertyPeer == null) {
			throw new CssInvalidValueException("Peer not found for property class: " + propertyClass, null, -1);
		}
		return propertyPeer.getStyleString(classLoader, componentClass, propertyValue);
	}

	/**
	 * Returns the <code>CssPropertyPeer</code> for the given property class.
	 * 
	 * @param propertyClass
	 *            the property class
	 * @return the XML parsing peer
	 */
	public CssPropertyPeer getPropertyPeer(Class propertyClass) {
		return (CssPropertyPeer) propertyPeerFactory.getPeerForObject(propertyClass, false);
	}
}
