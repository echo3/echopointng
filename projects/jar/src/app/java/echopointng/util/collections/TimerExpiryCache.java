package echopointng.util.collections;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <code>TimerExpiryCache</code> is an a implementation
 * of <code>ExpiryCache</code> that can contain
 * objects that "expire".  A <b>shared</b> background task will 
 * periodically "reap" objects that have "expired". 
 * <p>
 * By default, soft references are used to the cached data so that they can 
 * be reclaimed in low memory conditions regardless of whether they 
 * have expired or not.
 * <p>
 * The time-to-live and access-timeout is used to decide when an object 
 * has expired and needs to be removed from the cache. 
 * <p>
 * Time-to-live is simple.  Once the specified period elapses, the object 
 * is removed from the cache, regardless of how many times its been
 * accessed.  
 * <p>
 * Access-timeout is a little more complicated.  Each time the object 
 * is taken from the cache, its lastAccessTime is tracked.  If the
 * access-timeout has expired (since its last access) then the object
 * is taken from the cache.
 * <p>
 * If both the time-to-live and access-timeout is -1, then the object
 * will never expire from the cache.
 */
public class TimerExpiryCache extends ExpiryCache {

	/** 
	 * The reaper interval is 2 minutes.  Therefore the cached object 
	 * life span granulairty is +/- 1 minute. 
	 */
	public static final long DEFAULT_REAPER_INTERVAL = 2 * 60 * 1000;

	/** This is an array of all ExpiryCaches that are to be reaped */
	private static List allExpiryCaches;
	
	/** 
	 * The TimerExpiryCache reaper runs as a TimerTask under this static Timer.  
	 * All TimerExpiryCache instances share this common reaper.
	 */
	private static Timer reaperTimer;
	static {
		allExpiryCaches = new ArrayList();
		reaperTimer = new Timer(true);
		reaperTimer.schedule(new ReaperTimerTask(), DEFAULT_REAPER_INTERVAL, DEFAULT_REAPER_INTERVAL);
	}

	/**
	 * Constructs a default <code>TimerExpiryCache</code>
	 */
	public TimerExpiryCache() {
		this(DEFAULT_TIME_TO_LIVE,DEFAULT_ACCESS_TIMEOUT,true);
	}

	/**
	 * Constructs a <code>TimerExpiryCache</code>
	 * 
	 * @param timeToLive - the default time-to-live for a cache entry
	 * @param accessTimeout - the default access timeout for a cache entry
	 */
	public TimerExpiryCache(long timeToLive, long accessTimeout) {
		this(timeToLive,accessTimeout,true);
	}

	/**
	 * Constructs a <code>TimerExpiryCache</code> with all the parameters
	 * 
	 * @param timeToLive - the default time-to-live for a cache entry
	 * @param accessTimeout - the default access timeout for a cache entry
	 * @param softReferences - whether soft refernces are used to cached data
	 */
	public TimerExpiryCache(long timeToLive, long accessTimeout, boolean softReferences) {
		super(timeToLive,accessTimeout,softReferences);
		// add ourselves to the queue of TimerExpiryCaches for the reaper
		synchronized (allExpiryCaches) {
			allExpiryCaches.add(new WeakReference(this));
		}
	}

	/** A shared TimrTask that reapers all of the various ExpiryCaches */
	private static class ReaperTimerTask extends TimerTask {
		
		public void run() {
			//System.err.println("Cleanup");
			Thread currentThread = Thread.currentThread();
			if (currentThread.getPriority() != Thread.MIN_PRIORITY)
				currentThread.setPriority(Thread.MIN_PRIORITY);
			
			synchronized (allExpiryCaches) {
				for (Iterator iter = allExpiryCaches.iterator(); iter.hasNext();) {
					WeakReference weakRef = (WeakReference) iter.next();
					TimerExpiryCache expiryCache = (TimerExpiryCache) (weakRef == null ? null : weakRef.get());
					//
					// our global list is weakly referenced to the ExpiryCaches.  If
					// its gone, then no one else was interested in the cache
					// and neither are we
					//
					if (expiryCache == null) {
						iter.remove();
						continue;
					}
	
					List list = new ArrayList();
					synchronized (expiryCache) { 
						long now = System.currentTimeMillis();
						Set keys = expiryCache.keySet();
						Iterator itr = keys.iterator(); // Must be in synchronized block
						while (itr.hasNext()) {
							Object key = itr.next();
							if (expiryCache.hasExpired(key,now)) {
								list.add(key);
							}
						}
						for (Iterator iter2 = list.iterator(); iter2.hasNext();) {
							//System.err.println("Removing :" + key);
							Object key = iter2.next();
							expiryCache.put(key,null);
						}
					}
				}	
			}				
		}
	};
}
