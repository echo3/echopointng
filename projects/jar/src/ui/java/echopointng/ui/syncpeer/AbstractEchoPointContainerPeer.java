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
 * decision by deletoing the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
package echopointng.ui.syncpeer;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.update.ServerComponentUpdate;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;


/**
 * <code>AbstractEchoPointContainerPeer</code> is a base class for
 * peers, that provides a default implementation
 * of "container" handling for DomAdd, DomUpdate etc..
 * <p>
 * Child components are wrapped in a "container" element which allows
 * the framework to "add/update/remove" them easily. 
 */
public abstract class AbstractEchoPointContainerPeer extends AbstractEchoPointPeer {

	public AbstractEchoPointContainerPeer() {
		super();
	}
	
    /**
     * Returns an identifier in the form "parentId_contains_childId".
     * 
     * @see nextapp.echo2.webcontainer.SynchronizePeer#getContainerId(nextapp.echo2.app.Component)
     */
    public String getContainerId(Component child) {
        String parentId = ContainerInstance.getElementId(child.getParent());
        return parentId + "_contains_" + ContainerInstance.getElementId(child);        
    }
    
    /**
     * This looks for children with updated layour data or updated properties
     * on the peer's component and if there are any, it removes and
     * redraws the entire peer otherwise it just replaces the children
     * and the redraws them.  This method ends up calling
     * the ends <code>renderUpdate(rc, update, targetId, fullReplace)</code> 
     * method with an appropriate value for <code>fullReplace</code>.
     * 
     * @see nextapp.echo2.webcontainer.SynchronizePeer#renderUpdate(nextapp.echo2.webcontainer.RenderContext, 
     *      nextapp.echo2.app.update.ServerComponentUpdate, java.lang.String)
     */
    public boolean renderUpdate(RenderContext rc, ServerComponentUpdate update, String targetId) {
        boolean fullReplace = false;
        if (update.hasUpdatedLayoutDataChildren()) {
            fullReplace = true;
        }
		if (update.hasUpdatedProperties()) {
            if (partialUpdateManager.canProcess(rc, update)) {
            	partialUpdateManager.process(rc,update);
             } else {
             	fullReplace = true;
             }
        }
		return renderUpdateBaseImpl(rc, update, targetId, fullReplace);
    }
 }
