package echopointng;
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
import java.util.EventListener;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.event.EventListenerList;
import echopointng.externalevent.ExternalEvent;
import echopointng.externalevent.ExternalEventListener;
import echopointng.externalevent.ExternalEventMonitorService;

/**
 * A <code>ExternalEventMonitor</code> can be used to monitor
 * external events that come in via web request URIs. 
 * <p>
 * <code>ExternalEventMonitor</code> will inform 
 * all attached <code>ExternalEventListener</code>s about 
 * those web request events.
 * <p>
 * <code>ExternalEventMonitor</code> looks for web application 
 * requests with the special marker "E_id=ExternalEvent".  This 
 * allows other web pages (especially non Echo2 based ones) to link 
 * to the Echo web application via an URI, say something like :
 * <p>
 * <blockquote><pre>
 * /servletcontext/servlet?serviceId=ExternalEvent&amp;p1=v1&amp;p2=v2..
 * </pre></blockquote>
 * <p>
 * The parameters of the web request are packaged into a Map and 
 * placed within the <code>ExternalEvent</code> object.
 * <p>
 * <code>ExternalEventMonitor</code> is a non visual component.  
 * 
 * @see echopointng.externalevent.ExternalEvent
 * @see echopointng.externalevent.ExternalEventListener
 */
public class ExternalEventMonitor extends Component {
	
	private EventListenerList listenerList = new EventListenerList();
	
	/**
	 * Constructs an <code>ExternalEventMonitor</code> 
	 */
	public ExternalEventMonitor() {
		setVisible(false);
	}
	
	/**
	 * When the <code>ExternalEventMonitor</code> is placed in the
	 * component hierarchy it is also made known to the
	 * <code>ExternalEventMonitorService</code>
	 * 
	 * @see nextapp.echo2.app.Component#init()
	 */
	public void init() {
		super.init();
		ExternalEventMonitorService.INSTANCE.register(this);
	}
	 /**
	 * When the <code>ExternalEventMonitor</code> is removed from the
	 * component hierarchy it is also made removed from the
	 * <code>ExternalEventMonitorService</code>
	 * 
	 * @see nextapp.echo2.app.Component#dispose()
	 */
	public void dispose() {
		super.dispose();
		ExternalEventMonitorService.INSTANCE.deregister(this);
	}
	/**
	 * Adds an <code>ExternalEventListener</code> to the <code>ExternalEventMonitor</code>.
	 * @param l the ExternalEventListener to add
	 */
	/**
	 * Adds an <code>ExternalEventListener</code> to the <code>ExternalEventMonitor</code> 
	 * @param l the ExternalEventListener to add
	 */
	public void addExternalEventListener(ExternalEventListener l) {
		listenerList.addListener(ExternalEventListener.class,l);
	}
	/**
	 * Removes an <code>ExternalEventListener</code> from the <code>ExternalEventMonitor</code> 
	 * @param l the ExternalEventListener to remove
	 */
	public void removeExternalEventListener(ExternalEventListener l) {
		listenerList.removeListener(ExternalEventListener.class,l);
	}
	
	/**
	 * This is called by the support code to inform all
	 * ExternalEventListeners that an external event has 
	 * ocurred.  This is method not designed to be called 
	 * by Echo2 applications directly.
	 *  
	 * @param externalEvent - the new ExternalEvent
	 */
	public void fireExternalEvent(ExternalEvent externalEvent) {
		EventListener[] listeners = listenerList.getListeners(ExternalEventListener.class);
		for (int index = 0; index < listeners.length; ++index) {
			((ExternalEventListener) listeners[index]).externalEvent(externalEvent);
		}
	}
	
	/**
	 * This component can never be made visible.
	 * 
	 * @see nextapp.echo2.app.Component#setVisible(boolean)
	 */
	public void setVisible(boolean newValue) {
		super.setVisible(false);
	}

}
