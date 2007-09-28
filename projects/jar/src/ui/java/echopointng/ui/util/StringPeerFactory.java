package echopointng.ui.util;
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

/*
 * This class is take almost directly from the base Echo 2 class library
 * and hence is Copyright (C) 2002-2005 NextApp, Inc.  It was made part of 
 * this code base on 11/05/2005 
 */

/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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


import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nextapp.echo2.app.util.PropertiesDiscovery;

/**
 * A PeerFactory that can return and peer object instance
 * for a given string (rather than use class name
 * which is what PeerFactory does)
 */
public class StringPeerFactory {
    
    private final Map stringToPeerMap = new HashMap();
    
    public StringPeerFactory(String resourceName, ClassLoader classLoader) {
        try {
            Map peerNameMap = PropertiesDiscovery.loadProperties(resourceName, classLoader);
            Iterator it = peerNameMap.keySet().iterator();
            while (it.hasNext()) {
                String stringValue = (String) it.next();
                String peerClassName = (String) peerNameMap.get(stringValue);
                Class peerClass = classLoader.loadClass(peerClassName);
                Object peer = peerClass.newInstance();
                stringToPeerMap.put(stringValue, peer);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Unable to load string peer bindings: " + ex);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load string peer bindings: " + ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException("Unable to load string peer bindings: " + ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to load string peer bindings: " + ex);
        }
    }
    
    
    /**
     * Retrieves the appropriate peer instance for a given 
     * <code>String</code>.
     * 
     * @param matchString - the string to match
     * @return the relevant peer, or null if none can be found
     */
    public Object getPeer(String matchString) {
        Object peer = null;
        peer = stringToPeerMap.get(matchString);
        if (peer != null) {
            return peer;
        }
        return null;
    }
}
