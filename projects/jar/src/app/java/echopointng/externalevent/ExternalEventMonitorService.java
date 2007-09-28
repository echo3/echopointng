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

package echopointng.externalevent;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.TaskQueueHandle;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webrender.Connection;
import nextapp.echo2.webrender.Service;
import nextapp.echo2.webrender.ServiceRegistry;
import nextapp.echo2.webrender.WebRenderServlet;
import echopointng.ExternalEventMonitor;

/**
 * This service is used to listen for external events that come in when
 * the user follows a URI that has ?serviceId=ExternalEvent on it.
 */
public class ExternalEventMonitorService 
implements Service {
    
	/**
	 * The singleton ExternalEventService monitoring service
	 */
    public static final ExternalEventMonitorService INSTANCE;
    static {
    	INSTANCE = new ExternalEventMonitorService();
        ServiceRegistry serviceRegistry = WebRenderServlet.getServiceRegistry();
        serviceRegistry.add(ExternalEventMonitorService.INSTANCE);
    }
     /**
     * @see nextapp.echo2.webrender.Service#getId()
     */
    public String getId() {
        return "ExternalEvent";
    }

    /**
     * @see nextapp.echo2.webrender.Service#getVersion()
     */
    public int getVersion() {
        return DO_NOT_CACHE;
    }
    /** a weak map of external event monitors that want to know about events */
	private WeakHashMap weakInterestedParties = new WeakHashMap();
	
	/** a weak map of applcation instances to task queues */
	private WeakHashMap weakInstanceQueues = new WeakHashMap();
	

	/**
	 * Registers the <code>ExternalEventMonitor</code> with the service
	 * that is used to invoke external events.
	 * 
	 * @param monitor an <code>ExternalEventMonitor</code> to be notified
	 * of external events.
	 */
	public synchronized void register(ExternalEventMonitor monitor) {
		weakInterestedParties.put(monitor,null);
	}

	/**
	 * Deregisters the <code>ExternalEventMonitor</code> with the service
	 * that is used to invoke external events.
	 * 
	 * @param monitor an <code>ExternalEventMonitor</code> to be removed from
	 * being notified of external events.
	 */
	public synchronized void deregister(ExternalEventMonitor monitor) {
		weakInterestedParties.remove(monitor);
	}
    

    /**
     * @see nextapp.echo2.webrender.Service#service(nextapp.echo2.webrender.Connection)
     */
    public void service(Connection conn) throws IOException {
    	HttpServletRequest request = conn.getRequest();
    	ContainerInstance ci = (ContainerInstance) conn.getUserInstance();
        ApplicationInstance appInstance = ci.getApplicationInstance();
		if (appInstance != null) {
			synchronized(this) {
				Map parameterMap = new HashMap();
				for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
					String paramName = (String) e.nextElement();
					String[] paramValues = conn.getRequest().getParameterValues(paramName);
					parameterMap.put(paramName,paramValues);
				}
				final ExternalEvent externalEvent = new ExternalEvent(this,parameterMap);
				
				//
				// create a TaskQueue but only once per app instance.  As it is weak
				// it will die with the AppInstance and hence so will the TaskQueue
				// as its referred to by its AppInstance.
				TaskQueueHandle taskQueueHandle = (TaskQueueHandle) weakInstanceQueues.get(appInstance);
				if (taskQueueHandle == null) {
					taskQueueHandle = appInstance.createTaskQueue();
					weakInstanceQueues.put(appInstance,taskQueueHandle);
				}
				//
				// run through all registed event monitors but only tell the ones
				// that belong to the current app instance.
				Set set = weakInterestedParties.keySet();
				for (Iterator iter = set.iterator(); iter.hasNext();) {
					final ExternalEventMonitor monitor = (ExternalEventMonitor) iter.next();
					
					if (appInstance.equals(monitor.getApplicationInstance())) {
						Runnable task = new Runnable() {
							public void run() {
								monitor.fireExternalEvent(externalEvent);
							}
						};
						// tell the peer and hence the listeners about the event but in
						// a runnable task so that it executes in the main UI thread.
						appInstance.enqueueTask(taskQueueHandle,task);
					}
				}
			}
		}
		//
		// and then redirect them back to the Echo web app
		redirectToEchoApp(conn);}
    
	/**
	 * Redirects back to the actual Echo web application so that
	 * the user sees something.  It sends back all the
	 * parameters except the serviceId=ExternalEvent
	 *  
	 * @param conn - the connection in play
	 * @throws IOException
	 */	
	private void redirectToEchoApp(Connection conn) throws IOException {
		HttpServletRequest request = conn.getRequest();
		String uri = request.getRequestURI();
		StringBuffer parameters = new StringBuffer();
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = (String) e.nextElement();
			String[] values = conn.getRequest().getParameterValues(paramName);
			//
			// we dont send the serviceId=ExternalEvent again because we 
			// will then get invoked again.  But we do send everything else!
			if (! paramName.equals("serviceId")) {
				for (int i = 0; i < values.length; i++) {
					if (parameters.length() == 0)
						parameters.append('?');
					else
						parameters.append('&');
					parameters.append(paramName);	
					parameters.append('=');	
					parameters.append(values[i]);	
				}	
			}
		}
		uri = uri + parameters.toString();
	
		// 
		// Not sure which is a better way to redirect
		// from this "temporary" page.  sendRedirect() works
		// however I have seen Internet comments that
		// question this.
		if (true) {
			conn.getResponse().sendRedirect(uri);
		} else {
			RequestDispatcher dispatcher = conn.getServlet().getServletContext().getRequestDispatcher(uri);
			try { 
				if (dispatcher == null )
					throw new IOException("No Request Dispatcher for " + uri);
				dispatcher.forward(conn.getRequest(),conn.getResponse());
			} catch (javax.servlet.ServletException se) {
				throw new IOException("Dispatch ServletException : " + se.toString());
			}
		}
	}    
}
