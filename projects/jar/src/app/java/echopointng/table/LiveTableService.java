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
package echopointng.table;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import nextapp.echo2.app.Style;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.ContentType;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.ServiceRegistry;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.HtmlDocument;
import nextapp.echo2.webrender.output.XmlDocument;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import echopointng.EPNG;
import echopointng.LiveTable;

/** 
 * <code>LiveTableService</code> is used to provide page
 * data to a <code>LiveTable</code> component in an asynchronous manner.
 */

public class LiveTableService implements Service {
	
	/**
	 * The singleton <code>LiveTableService</code>
	 */
    public static final LiveTableService INSTANCE;
    static {
    	INSTANCE = new LiveTableService();
        ServiceRegistry serviceRegistry = WebRenderServlet.getServiceRegistry();
        serviceRegistry.add(LiveTableService.INSTANCE);
    }
     /**
     * @see nextapp.echo2.webrender.Service#getId()
     */
    public String getId() {
        return "EPNG.LiveTable";
    }

    /**
     * @see nextapp.echo2.webrender.Service#getVersion()
     */
    public int getVersion() {
        return DO_NOT_CACHE;
    }
    
	private Map interestedParties = new HashMap();
	

	/**
	 * Registers the <code>LiveTable</code> with the service
	 * 
	 * @param liveTable -  a <code>LiveTable</code> to be notified
	 * of external events.
	 */
	public synchronized void register(LiveTable liveTable) {
		interestedParties.put("c_" + liveTable.getRenderId(),liveTable);
	}

	/**
	 * Deregisters the <code>LiveTable</code> with the service
	 * 
	 * @param liveTable -  an <code>LiveTable</code> to be removed from
	 * being notified of external events.
	 */
	public synchronized void deregister(LiveTable liveTable) {
		interestedParties.remove(liveTable);
	}
    

    /**
     * @see nextapp.echo2.webrender.Service#service(nextapp.echo2.webrender.Connection)
     */
    public void service(Connection conn) throws IOException {
    	HttpServletRequest request = conn.getRequest();
        
        String elementId = request.getParameter("elementId");
        LiveTable liveTable = (LiveTable) interestedParties.get(elementId);
        if (liveTable == null) {
        	throw new IllegalStateException("The LiveTable " + elementId + " could not be found.");
        }
        LiveTableRenderer liveTableRenderer = (LiveTableRenderer) getRP(liveTable,LiveTable.PROPERTY_RENDERER);

        XmlDocument xmlDocument = new XmlDocument("data", null, null, HtmlDocument.XHTML_1_0_NAMESPACE_URI);
        Document document = xmlDocument.getDocument();
        Element dataElement = document.getDocumentElement();
        
        int currentPage = Integer.parseInt(request.getParameter("currentPage"));
        int rowsPerPage = getRP(liveTable,LiveTable.PROPERTY_ROWS_PER_PAGE, 100);
        int pageFetchSize = getRP(liveTable,LiveTable.PROPERTY_PAGE_FETCH_SIZE, 2);
        
        for (int i = 0; i < pageFetchSize; i++) {
            int rowStart = (currentPage + i) * rowsPerPage; 
            int rowEnd = rowStart + rowsPerPage;
            
            Element tbody = document.createElement("tbody");
            dataElement.appendChild(tbody);
            
            liveTableRenderer.renderRows(liveTable,tbody,rowStart,rowEnd);
		}
        	
        conn.setContentType(ContentType.TEXT_XML);
        PrintWriter out = conn.getWriter();
        xmlDocument.render(out);
    }
    
    private static Object getRP(LiveTable liveTable, String propertyName) {
    	Object value = liveTable.getRenderProperty(propertyName);
    	if (value == null) {
    		Style style = EPNG.getFallBackStyle(liveTable);
    		if (style != null) {
    			value = style.getProperty(propertyName);
    		}
    	}
    	return value;
    }
    
    private static int getRP(LiveTable liveTable, String propertyName, int fallbackValue) {
    	Object value = liveTable.getRenderProperty(propertyName);
    	if (value == null) {
    		Style style = EPNG.getFallBackStyle(liveTable);
    		if (style != null) {
    			value = style.getProperty(propertyName);
    			if (value == null) {
    				return fallbackValue;
    			}
    		}
    	}
    	return ((Integer) value).intValue();
    }

}
