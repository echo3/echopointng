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
package echopointng.ui.template;

import java.util.Map;
import java.util.WeakHashMap;

import echopointng.ui.util.StringPeerFactory;

/**
 * <code>TemplateCompilerLoader</code> is repsonsible for finding
 * a template compiler instance for a given content type
 */
public class TemplateCompilerLoader {
	
    private static final String PEERS_PATH = "META-INF/echopointng/template/TemplateCompilers.properties";
    
    private static final Map classLoaderToLoaderMap = new WeakHashMap();
    
    /**
     * Creates or retrieves a <code>TemplateCompilerLoader</code>
     * givena <code>ClassLoader</code>.
     * 
     * @param classLoader the <code>ClassLoader</code> to use for 
     *        dynamically loading classes
     * 
     * @return the <code>TemplateCompilerLoader</code>
     */
    public static TemplateCompilerLoader forClassLoader(ClassLoader classLoader) {
        synchronized(classLoaderToLoaderMap) {
        	TemplateCompilerLoader loader = (TemplateCompilerLoader) classLoaderToLoaderMap.get(classLoader);
            if (loader == null) {
                loader = new TemplateCompilerLoader(classLoader);
                classLoaderToLoaderMap.put(classLoader, loader);
            }
            return loader;
        }
    }

    private StringPeerFactory peerFactory;
    
    /**
     * Creates a new <code>TemplateCompilerLoader</code>.
     * 
     * @param classLoader the <code>ClassLoader</code> to use for 
     *        dynamically loading property classes
     */
    private TemplateCompilerLoader(ClassLoader classLoader) {
        super();
        peerFactory = new StringPeerFactory(PEERS_PATH, classLoader);
    }
    
    /**
     * Returns the <code>TemplateCompiler</code> that can compile
     * the given content type.
     * 
     * @param contentType - the content type of the template data
     * @return the XML parsing peer
     */
    public TemplateCompiler getTemplateCompiler(String contentType) {
        return (TemplateCompiler) peerFactory.getPeer(contentType);
    }
}
