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
import java.io.IOException;

import nextapp.echo2.app.Extent;
import nextapp.echo2.app.ResourceImageReference;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.Service;

/**
 * <code>StaticImageService</code> provides a static service
 * wrapper for a given resource based image reference.
 */

public class StaticImageService implements Service {
    
    public static StaticImageService forResource(String id, String resourceName) {
        ResourceImageReference imageRef = new ResourceImageReference(resourceName);
        return new StaticImageService(id, imageRef);
    }

    public static StaticImageService forResource(String id, String resourceName, Extent width, Extent height) {
        ResourceImageReference imageRef = new ResourceImageReference(resourceName,width,height);
        return new StaticImageService(id, imageRef);
    }
    
    private String id;
    private ResourceImageReference imageRef;
    
    public StaticImageService(String id, ResourceImageReference imageRef) {
        super();
        this.id = id;
        this.imageRef = imageRef;
    }
    
    /**
     * @see nextapp.echo2.webrender.server.Service#getId()
     */
    public String getId() {
        return id;
    }
    
    /**
     * @see nextapp.echo2.webrender.server.Service#getVersion()
     */
    public int getVersion() {
        return 0;
    }
    
    /**
     * @see nextapp.echo2.webrender.server.Service#service(nextapp.echo2.webrender.server.Connection)
     */
    public void service(Connection conn) throws IOException {
        conn.getResponse().setContentType(imageRef.getContentType());
        imageRef.render(conn.getResponse().getOutputStream());
    }
}
